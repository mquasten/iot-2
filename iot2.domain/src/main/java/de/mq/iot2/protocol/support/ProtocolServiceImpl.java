package de.mq.iot2.protocol.support;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;

@Service
class ProtocolServiceImpl implements ProtocolService {

	static final String MESSAGE_CONVERTER_MISSING = "Class %s can not be converted to string.";
	static final String MESSAGE_VALUE_RREQUIRED = "Value is Rrequired.";
	static final String MESSAGE_KEY_RREQUIRED = "Key is Rrequired.";
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

	private ProtocolParameterImpl convert(final Protocol protocol, final ProtocolParameterType type, Entry<? extends Enum<?>, Object> entry) {
		Assert.notNull(entry.getKey(), MESSAGE_KEY_RREQUIRED);
		Assert.notNull(entry.getValue(), MESSAGE_VALUE_RREQUIRED);
		Assert.isTrue(conversionService.canConvert(entry.getValue().getClass(), String.class), String.format(MESSAGE_CONVERTER_MISSING, entry.getValue().getClass().getSimpleName()));
		return new ProtocolParameterImpl(protocol, conversionService.convert(entry.getKey(), String.class), type, conversionService.convert(entry.getValue(), String.class));
	}

}
