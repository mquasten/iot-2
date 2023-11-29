package de.mq.iot2.protocol.support;


import org.springframework.stereotype.Component;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

@Component
class ProtocolMapper implements ModelMapper<Protocol,ProtocolModel> {

	@Override
	public ProtocolModel toWeb(final Protocol protocol) {
		final ProtocolModel protocolModel = new ProtocolModel();
		protocolModel.setId(IdUtil.getId(protocol));
		protocolModel.setName(protocol.name());
		protocolModel.setExecutionTime(protocol.executionTime());
		protocolModel.setStatus(protocol.status());
		
		protocolModel.setLogMessage(protocol.logMessage().orElse(null));
		return protocolModel;
	}

}
