package de.mq.iot2.protocol;


import java.util.Map;



import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;


public interface ProtocolService {

	Protocol create(final String name);

	void assignParameter(final Protocol protocol, final ProtocolParameterType rulesengineargument, final Map<? extends Enum<?>, Object> arguments );




}
