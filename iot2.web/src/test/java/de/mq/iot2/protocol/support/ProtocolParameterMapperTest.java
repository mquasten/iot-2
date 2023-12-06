package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.support.IdUtil;

class ProtocolParameterMapperTest {

	private static final String PARAMETER_VALUE = "5.04";
	private static final String PARAMETER_NAME = "Temperature";
	private static final String PROTOCOL_NAME = "end-of-day";
	private static final String PROTOCOL_ID = UUID.randomUUID().toString();
	private final ProtocolParameterMapper protocolParameterMapper = new ProtocolParameterMapper();
	private final Protocol protocol = new ProtocolImpl(PROTOCOL_NAME);

	@Test
	void toWebSystemvariableProtocolParameter() {
		IdUtil.assignId(protocol, PROTOCOL_ID);
		final SystemvariableProtocolParameter systemvariableProtocolParameter = new SystemvariableProtocolParameterImpl(protocol, PARAMETER_NAME, PARAMETER_VALUE);

		final ProtocolParameterModel result = protocolParameterMapper.toWeb(systemvariableProtocolParameter);

		assertEquals(PROTOCOL_ID, result.getProtocolId());
		assertEquals(PROTOCOL_NAME, result.getProtocolName());
		assertEquals(PARAMETER_NAME, result.getName());
		assertEquals(systemvariableProtocolParameter.type(), result.getType());
		assertEquals(systemvariableProtocolParameter.status(), result.getStatus());
		assertEquals(PARAMETER_VALUE, result.getValue());
		assertTrue(result.isSystemvariableParameter());
	}

	@Test
	void toWebr() {
		IdUtil.assignId(protocol, PROTOCOL_ID);

		final ProtocolParameter protocolParameter = new ProtocolParameterImpl(protocol, PARAMETER_NAME, ProtocolParameterType.IntermediateResult, PARAMETER_VALUE);

		final ProtocolParameterModel result = protocolParameterMapper.toWeb(protocolParameter);

		assertEquals(PROTOCOL_ID, result.getProtocolId());
		assertEquals(PROTOCOL_NAME, result.getProtocolName());
		assertEquals(PARAMETER_NAME, result.getName());
		assertEquals(ProtocolParameterType.IntermediateResult, result.getType());
		assertNull(result.getStatus());
		assertEquals(PARAMETER_VALUE, result.getValue());
		assertFalse(result.isSystemvariableParameter());
	}

}
