package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.support.ProtocolParameterImpl.MESSAGE_NAME_IS_REQUIRED;
import static de.mq.iot2.protocol.support.ProtocolParameterImpl.MESSAGE_PROTOCOL_IS_REQUIRED;
import static de.mq.iot2.protocol.support.ProtocolParameterImpl.MESSAGE_TYPE_IST_REQUIRED;
import static de.mq.iot2.protocol.support.ProtocolParameterImpl.MESSAGE_VALUE_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.support.RandomTestUtil;

class ProtocolParameterImplTest {

	private final Protocol protocol = new ProtocolImpl(RandomTestUtil.randomString());

	private final String name = RandomTestUtil.randomString();
	private final String value = RandomTestUtil.randomString();

	private final ProtocolParameter protocolParameter = newProtocolParameter();

	private ProtocolParameterImpl newProtocolParameter() {
		return new ProtocolParameterImpl(protocol, name, ProtocolParameterType.Input, value);
	}

	private final ProtocolParameter emptyProtocolParameter = BeanUtils.instantiateClass(ProtocolParameterImpl.class);

	@Test
	void protocol() {
		assertEquals(protocol, protocolParameter.protocol());
	}

	@Test
	void protocolNull() {
		assertEquals(MESSAGE_PROTOCOL_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> emptyProtocolParameter.protocol()).getMessage());

	}

	@Test
	void name() {
		assertEquals(name, protocolParameter.name());
	}

	@Test
	void nameNull() {
		assertEquals(MESSAGE_NAME_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> emptyProtocolParameter.name()).getMessage());
	}

	@Test
	void type() {
		assertEquals(ProtocolParameterType.Input, protocolParameter.type());
	}

	@Test
	void typeNull() {
		assertEquals(MESSAGE_TYPE_IST_REQUIRED, assertThrows(IllegalArgumentException.class, () -> emptyProtocolParameter.type()).getMessage());
	}

	@Test
	void value() {
		assertEquals(value, protocolParameter.value());
	}

	@Test
	void valueNull() {
		assertEquals(MESSAGE_VALUE_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> emptyProtocolParameter.value()).getMessage());
	}

	@Test
	void constructorGuards() {
		assertEquals(MESSAGE_PROTOCOL_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> new ProtocolParameterImpl(null, name, ProtocolParameterType.Input, value)).getMessage());
		assertEquals(MESSAGE_NAME_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> new ProtocolParameterImpl(protocol, null, ProtocolParameterType.IntermediateResult, value)).getMessage());
		assertEquals(MESSAGE_TYPE_IST_REQUIRED, assertThrows(IllegalArgumentException.class, () -> new ProtocolParameterImpl(protocol, name, null, value)).getMessage());
		assertEquals(MESSAGE_VALUE_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> new ProtocolParameterImpl(protocol, name, ProtocolParameterType.Result, null)).getMessage());
	}

	@Test
	void hash() {
		assertEquals(name.hashCode() + protocol.hashCode(), protocolParameter.hashCode());

		assertEquals(System.identityHashCode(emptyProtocolParameter), emptyProtocolParameter.hashCode());

		final ProtocolParameter invalid = BeanUtils.instantiateClass(ProtocolParameterImpl.class);
		assertEquals(System.identityHashCode(invalid), invalid.hashCode());

		setName(invalid, name);
		assertEquals(System.identityHashCode(invalid), invalid.hashCode());

		setName(invalid, null);
		setProtocoll(invalid, protocol);
		assertEquals(System.identityHashCode(invalid), invalid.hashCode());

	}

	private void setName(final ProtocolParameter protocolParameter, String name) {
		ReflectionTestUtils.setField(protocolParameter, "name", name);
	}

	private void setProtocoll(final ProtocolParameter protocolParameter, Protocol protocol) {
		ReflectionTestUtils.setField(protocolParameter, "protocol", protocol);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void equals() throws InterruptedException {
		final ProtocolParameter protocolParameter = newProtocolParameter();

		final ProtocolParameter other = newProtocolParameter();

		assertTrue(protocolParameter.equals(other));

		setProtocoll(other, new ProtocolImpl(RandomTestUtil.randomString()));
		assertFalse(protocolParameter.equals(other));

		setProtocoll(other, protocol);
		setName(other, RandomTestUtil.randomString());
		assertFalse(protocolParameter.equals(other));

		setProtocoll(other, new ProtocolImpl(RandomTestUtil.randomString()));
		assertFalse(protocolParameter.equals(other));

		final ProtocolParameter otherProtocolParameter = BeanUtils.instantiateClass(ProtocolParameterImpl.class);
		assertFalse(protocolParameter.equals(otherProtocolParameter));
		assertFalse(otherProtocolParameter.equals(protocolParameter));
		assertFalse(otherProtocolParameter.equals(BeanUtils.instantiateClass(ProtocolParameterImpl.class)));
		assertTrue(otherProtocolParameter.equals(otherProtocolParameter));
		assertFalse(protocolParameter.equals(new String()));

	}

}
