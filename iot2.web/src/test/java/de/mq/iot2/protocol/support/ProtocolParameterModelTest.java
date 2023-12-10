package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus;

class ProtocolParameterModelTest {

	private final ProtocolParameterModel protocolParameterModel = new ProtocolParameterModel();


	private String random() {
		return RandomStringUtils.random(50);
	}

	@Test
	void name() {
		assertNull(protocolParameterModel.getName());

		final var name = random();
		protocolParameterModel.setName(name);
		assertEquals(name, protocolParameterModel.getName());
	}

	@Test
	void type() {
		assertNull(protocolParameterModel.getType());

		protocolParameterModel.setType(ProtocolParameterType.IntermediateResult);
		assertEquals(ProtocolParameterType.IntermediateResult, protocolParameterModel.getType());
	}

	@Test
	void value() {
		assertNull(protocolParameterModel.getValue());

		final var value = random();
		protocolParameterModel.setValue(value);
		assertEquals(value, protocolParameterModel.getValue());
	}

	@Test
	void status() {
		assertNull(protocolParameterModel.getStatus());

		protocolParameterModel.setStatus(SystemvariableStatus.Updated);
		assertEquals(SystemvariableStatus.Updated, protocolParameterModel.getStatus());
	}

	@Test
	void isSystemvariableParameter() {
		assertFalse(protocolParameterModel.isSystemvariableParameter());

		protocolParameterModel.setStatus(SystemvariableStatus.Calculated);
		assertTrue(protocolParameterModel.isSystemvariableParameter());
	}
}
