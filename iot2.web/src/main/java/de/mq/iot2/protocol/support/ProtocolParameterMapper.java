package de.mq.iot2.protocol.support;

import org.springframework.stereotype.Component;

import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

@Component
class ProtocolParameterMapper implements ModelMapper<ProtocolParameter, ProtocolParameterModel> {

	@Override
	public ProtocolParameterModel toWeb(final ProtocolParameter domain) {
		final ProtocolParameterModel protocolParameterModel = new ProtocolParameterModel();
		protocolParameterModel.setName(domain.name());
		protocolParameterModel.setValue(domain.value());
		protocolParameterModel.setType(domain.type());
		protocolParameterModel.setProtocolName(domain.protocol().name());
		protocolParameterModel.setProtocolId(IdUtil.getId(domain.protocol()));

		if (domain instanceof SystemvariableProtocolParameter) {
			protocolParameterModel.setStatus(((SystemvariableProtocolParameter) domain).status());
		}

		return protocolParameterModel;
	}

}
