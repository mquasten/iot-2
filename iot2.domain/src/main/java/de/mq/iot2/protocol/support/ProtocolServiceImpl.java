package de.mq.iot2.protocol.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.sysvars.SystemVariable;

@Service
class ProtocolServiceImpl implements ProtocolService {

	static final String MESSAGE_CONVERTER_MISSING = "Class %s can not be converted to string.";
	static final String MESSAGE_VALUE_RREQUIRED = "Value is Rrequired.";
	static final String MESSAGE_KEY_RREQUIRED = "Key is Required.";
	private final ProtocolRepository protocolRepository;
	private final ProtocolParameterRepository protocolParameterRepository;
	private final ConversionService conversionService;

	ProtocolServiceImpl(final ProtocolRepository protocolRepository, final ProtocolParameterRepository protocolParameterRepository, final ConversionService conversionService) {
		this.protocolRepository = protocolRepository;
		this.conversionService = conversionService;
		this.protocolParameterRepository = protocolParameterRepository;
	}

	@Override
	@Transactional
	public Protocol create(final String name) {
		final Protocol protocol = new ProtocolImpl(name);
		return protocolRepository.save(protocol);

	}

	@Override
	@Transactional
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
	public void assignParameter(final Protocol protocol, final Collection<SystemVariable> systemVariables) {
		systemVariables.stream(). map(systemVariable -> convert(protocol, systemVariable)).forEach(protocolParameter -> protocolParameterRepository.save(protocolParameter));
		
	}
	
	private ProtocolParameter convert(final Protocol protocol, final SystemVariable systemVariable) {
		return new SystemvariableProtocolParameterImpl(protocol, systemVariable.getName(), systemVariable.getValue());
	}
	
	@Override
	@Transactional
	public void updateSystemVariables(final Protocol protocol, final Collection<SystemVariable> systemVariables) {
		final String protocolId = IdUtil.getId(protocol);
		final List<String> updated = systemVariables.stream().map(SystemVariable::getName).collect(Collectors.toList());
		protocolParameterRepository.findByProtocolIdNameNameIn(protocolId, updated).forEach(parameter -> assignUpdated(parameter));
		
	}

	private void  assignUpdated(final SystemvariableProtocolParameter parameter) {
		parameter.assignUpdated();
		protocolParameterRepository.save(parameter);
	}

}
