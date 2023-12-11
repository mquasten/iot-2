package de.mq.iot2.protocol.support;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.sysvars.SystemVariable;

@Service
class ProtocolServiceImpl implements ProtocolService {

	static final String MESSAGE_PROTOCOL_NOT_FOUND_FOR_ID = "Protocol not found Id: %s";
	static final String MESSAGE_ID_REQUIRED = "Id required.";
	static final String MESSAGE_CONVERTER_MISSING = "Class %s can not be converted to string.";
	static final String MESSAGE_VALUE_RREQUIRED = "Value is Rrequired.";
	static final String MESSAGE_KEY_RREQUIRED = "Key is Required.";
	static final String MESSAGE_DAYS_BACK_INVALID = "DaysBack should be > 0.";
	private final ProtocolRepository protocolRepository;
	private final ProtocolParameterRepository protocolParameterRepository;
	private final ConversionService conversionService;
	private final Converter<Pair<ProtocolParameter, Boolean>, String[]> parameterCsvConverter;

	private final String csvDelimiter;

	ProtocolServiceImpl(final ProtocolRepository protocolRepository, final ProtocolParameterRepository protocolParameterRepository, final ConversionService conversionService,
			final Converter<Pair<ProtocolParameter, Boolean>, String[]> parameterCsvConverter, @Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.protocolRepository = protocolRepository;
		this.conversionService = conversionService;
		this.protocolParameterRepository = protocolParameterRepository;
		this.parameterCsvConverter = parameterCsvConverter;
		this.csvDelimiter = csvDelimiter;
	}

	@Override
	public Protocol protocol(final String name) {
		return new ProtocolImpl(name);
	}

	@Override
	@Transactional
	@CheckConfiguration
	public void save(final Protocol protocol) {
		protocolRepository.save(protocol);
	}

	@Override
	@Transactional
	@CheckConfiguration
	public void success(final Protocol protocol, final String message) {
		protocol.assignSuccessState();
		protocol.assignLogMessage(message);
		protocolRepository.save(protocol);

	}

	@CheckConfiguration
	public void success(final Protocol protocol) {
		success(protocol, null);

	}

	@Override
	@Transactional
	@CheckConfiguration
	public void error(final Protocol protocol, final Throwable throwable) {
		final StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		protocol.assignLogMessage(writer.toString());
		protocol.assignErrorState();
		protocolRepository.save(protocol);
	}

	@Override
	@Transactional
	@CheckConfiguration
	public void assignParameter(final Protocol protocol, final ProtocolParameterType type, final Map<? extends Enum<?>, Object> parameters) {
		parameters.entrySet().stream().map(entry -> convert(protocol, type, entry)).forEach(protocolParameter -> protocolParameterRepository.save(protocolParameter));
	}

	private ProtocolParameter convert(final Protocol protocol, final ProtocolParameterType type, Entry<? extends Enum<?>, Object> entry) {
		Assert.notNull(entry.getKey(), MESSAGE_KEY_RREQUIRED);
		Assert.notNull(entry.getValue(), MESSAGE_VALUE_RREQUIRED);
		Assert.isTrue(conversionService.canConvert(entry.getValue().getClass(), String.class), String.format(MESSAGE_CONVERTER_MISSING, entry.getValue().getClass().getSimpleName()));
		return new ProtocolParameterImpl(protocol, conversionService.convert(entry.getKey(), String.class), type, conversionService.convert(entry.getValue(), String.class));
	}

	@Override
	@Transactional
	@CheckConfiguration
	public void assignParameter(final Protocol protocol, final ProtocolParameterType type, final String name, final Object value) {
		Assert.notNull(name, MESSAGE_KEY_RREQUIRED);
		Assert.notNull(value, MESSAGE_VALUE_RREQUIRED);
		final ProtocolParameter protocolParameter = new ProtocolParameterImpl(protocol, name, type, conversionService.convert(value, String.class));
		protocolParameterRepository.save(protocolParameter);
	}

	@Override
	@Transactional
	@CheckConfiguration
	public void assignParameter(final Protocol protocol, final Collection<SystemVariable> systemVariables) {
		systemVariables.stream().map(systemVariable -> convert(protocol, systemVariable)).forEach(protocolParameter -> protocolParameterRepository.save(protocolParameter));

	}

	private ProtocolParameter convert(final Protocol protocol, final SystemVariable systemVariable) {
		return new SystemvariableProtocolParameterImpl(protocol, systemVariable.getName(), systemVariable.getValue());
	}

	@Override
	@Transactional
	@CheckConfiguration
	public void updateSystemVariables(final Protocol protocol, final Collection<SystemVariable> systemVariables) {
		final String protocolId = IdUtil.getId(protocol);
		final List<String> updated = systemVariables.stream().map(SystemVariable::getName).collect(Collectors.toList());
		protocolParameterRepository.findByProtocolIdNameNameIn(protocolId, updated).forEach(parameter -> assignUpdated(parameter));
	}

	private void assignUpdated(final SystemvariableProtocolParameter parameter) {
		parameter.assignUpdated();
		protocolParameterRepository.save(parameter);
	}

	@Override
	@Transactional
	public int deleteProtocols(final int daysBack) {
		Assert.isTrue(daysBack > 0, MESSAGE_DAYS_BACK_INVALID);
		final var deleteDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).minusDays(daysBack);

		final Collection<Protocol> toBeDeleted = protocolRepository.findByExecutionTimeBefore(deleteDate);
		toBeDeleted.forEach(this::deleteProtocol);
		return toBeDeleted.size();
	}

	private void deleteProtocol(final Protocol protocol) {
		protocolParameterRepository.findByProtocolOrderByTypeAscNameAsc(protocol).forEach(protocolParameterRepository::delete);
		protocolRepository.delete(protocol);
	}

	@Override
	@Transactional
	public Collection<String> protocolNames() {
		return protocolRepository.findDistinctNames();
	}

	@Override
	@Transactional
	public Collection<Protocol> protocols(final String name) {
		return protocolRepository.findByNameOrderByExecutionTime(name);
	}

	@Override
	@Transactional
	public Protocol protocolById(final String id) {
		Assert.hasText(id, MESSAGE_ID_REQUIRED);
		return protocolRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(String.format(MESSAGE_PROTOCOL_NOT_FOUND_FOR_ID, id), 1));
	}

	@Override
	@Transactional
	public Collection<ProtocolParameter> protocolParameters(final String protocolId) {
		return protocolParameterRepository.findByProtocolOrderByTypeAscNameAsc(protocolById(protocolId));
	}

	@Override
	@Transactional
	public void export(final OutputStream os) {
		try (final PrintWriter writer = new PrintWriter(os)) {
			final Collection<String> protocolIdsProcessed = new HashSet<>();
			protocolParameterRepository.findAll().stream().sorted((p1, p2) -> {
				final int compareProtocolIds = IdUtil.getId(p1.protocol()).compareTo(IdUtil.getId(p2.protocol()));
				if (compareProtocolIds != 0) {
					return compareProtocolIds;
				}

				return p1.name().compareTo(p2.name());

			}).forEach(parameter -> {
				final String protocolId = IdUtil.getId(parameter.protocol());
				writer.println(StringUtils.arrayToDelimitedString(parameterCsvConverter.convert(Pair.of(parameter, protocolIdsProcessed.contains(protocolId))), csvDelimiter));
				protocolIdsProcessed.add(protocolId);
			});
		}

	}

}
