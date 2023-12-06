package de.mq.iot2.protocol;


import java.util.Collection;
import java.util.Map;



import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.sysvars.SystemVariable;


public interface ProtocolService {

	Protocol protocol(final String name);
	
	void save(final Protocol protocol);
	void assignParameter(final Protocol protocol, final ProtocolParameterType rulesengineargument, final Map<? extends Enum<?>, Object> arguments );

	void assignParameter(final Protocol protocol, final Collection<SystemVariable> systemVariables);

	void updateSystemVariables(final Protocol protocol, final  Collection<SystemVariable> systemVariables);

	void success(final Protocol protocol);

	void error(final Protocol protocol, final Throwable throwable);

	void success(final Protocol protocol, final String message);

	void assignParameter(final Protocol protocol, final ProtocolParameterType type, final String name, final Object value);

	int deleteProtocols(final int daysBack);

	Collection<String> protocolNames();

	Collection<Protocol> protocols(final String name);

	Protocol protocolById(final String id);

	Collection<ProtocolParameter> protocolParameters(final String protocolId);
	







}
