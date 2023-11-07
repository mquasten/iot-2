package de.mq.iot2.protocol.support;

import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolService;

@Service
class ProtocolServiceImpl implements ProtocolService {

	private final ProtocolRepository protocolRepository;
	private final ProtocolParameterRepository protocolParameterRepository;
	private final ConversionService conversionService;

	ProtocolServiceImpl(final ProtocolRepository protocolRepository,final ProtocolParameterRepository protocolParameterRepository, final ConversionService conversionService) {
		this.protocolRepository = protocolRepository;
		this.conversionService=conversionService;
		this.protocolParameterRepository=protocolParameterRepository;
	}

	@Override
	@Transactional
	public Protocol create(final String name) {
		final Protocol protocol = new ProtocolImpl(name);
		return protocolRepository.save(protocol);

	}
	@Override
	@Transactional
	public void assignParameter(final Protocol protocol, final ProtocolParameter.ProtocolParameterType type,final Map<? extends Enum<?>, Object>  parameters) {
		parameters.entrySet().stream().map( e -> new ProtocolParameterImpl(protocol, conversionService.convert(e.getKey(), String.class), type, conversionService.convert(e.getKey(), String.class))).forEach( protocolParameter -> protocolParameterRepository.save(protocolParameter));
	}

}
