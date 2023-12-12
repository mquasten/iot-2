package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus.Calculated;
import static de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus.Updated;
import static de.mq.iot2.protocol.support.SystemvariableProtocolParameterImpl.MESSAGE_INVALID_STATUS;
import static de.mq.iot2.protocol.support.SystemvariableProtocolParameterImpl.MESSAGE_STATUS_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanUtils;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus;
import de.mq.iot2.support.RandomTestUtil;

class SystemvariableProtocolParameterImplTest {

	private final String name = RandomTestUtil.randomString();
	private final Protocol protocol = new ProtocolImpl(RandomTestUtil.randomString());
	private final String value = RandomTestUtil.randomString();

	private final SystemvariableProtocolParameter systemvariableProtocolProtocolParameter = new SystemvariableProtocolParameterImpl(protocol, name, value);

	@Test
	void type() {
		assertEquals(Result, systemvariableProtocolProtocolParameter.type());
	}

	@Test
	void status() {
		assertEquals(Calculated, systemvariableProtocolProtocolParameter.status());

		systemvariableProtocolProtocolParameter.assignUpdated();

		assertEquals(Updated, systemvariableProtocolProtocolParameter.status());

		assertEquals(MESSAGE_INVALID_STATUS, assertThrows(IllegalArgumentException.class, () -> systemvariableProtocolProtocolParameter.assignUpdated()).getMessage());
	}

	@Test
	void createEmpty() {
		final SystemvariableProtocolParameter systemvariableProtocolParameter = BeanUtils.instantiateClass(SystemvariableProtocolParameterImpl.class);
		assertEquals(MESSAGE_STATUS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> systemvariableProtocolParameter.status()).getMessage());
		assertEquals(MESSAGE_INVALID_STATUS, assertThrows(IllegalArgumentException.class, () -> systemvariableProtocolParameter.assignUpdated()).getMessage());
	}
	
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	void createWithEmptyValue(final String value) {
		final SystemvariableProtocolParameter systemvariableProtocolParameter = new SystemvariableProtocolParameterImpl(protocol, name, value);
		
		assertEquals(SystemvariableProtocolParameterImpl.EMPTY_VALUE_STRING, systemvariableProtocolParameter.value());
	}
	@Test
	void createImport() {
		final SystemvariableStatus status= SystemvariableStatus.Updated;
		
		final SystemvariableProtocolParameter systemvariableProtocolParameter=new SystemvariableProtocolParameterImpl(protocol,name, value, status );
		
		assertEquals(protocol, systemvariableProtocolParameter.protocol());
		assertEquals(name, systemvariableProtocolParameter.name());
		assertEquals(value, systemvariableProtocolParameter.value());
		assertEquals(status, systemvariableProtocolParameter.status());
		
	}
	
	

}
