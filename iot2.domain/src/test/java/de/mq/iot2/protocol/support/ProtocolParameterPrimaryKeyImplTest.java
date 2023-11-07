package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.support.RandomTestUtil;

class ProtocolParameterPrimaryKeyImplTest {
	private final String protocol = RandomTestUtil.randomString();

	private final String name = RandomTestUtil.randomString();

	private final ProtocolParameterPrimaryKeyImpl protocolParameterPrimaryKey = new ProtocolParameterPrimaryKeyImpl(name, protocol);

	private final ProtocolParameterPrimaryKeyImpl emptyProtocolParameterPrimaryKey = new ProtocolParameterPrimaryKeyImpl();

	@Test
	void name() {
		assertEquals(name, protocolParameterPrimaryKey.name);
		assertEquals(protocol, protocolParameterPrimaryKey.protocol);
	}

	@Test
	void hash() {
		assertEquals(name.hashCode() + protocol.hashCode(), protocolParameterPrimaryKey.hashCode());

		assertEquals(System.identityHashCode(emptyProtocolParameterPrimaryKey), emptyProtocolParameterPrimaryKey.hashCode());

		final ProtocolParameterPrimaryKeyImpl invalid = new ProtocolParameterPrimaryKeyImpl();
		assertEquals(System.identityHashCode(invalid), invalid.hashCode());

		setName(invalid, name);
		assertEquals(System.identityHashCode(invalid), invalid.hashCode());

		setName(invalid, null);
		setProtocoll(invalid, protocol);
		assertEquals(System.identityHashCode(invalid), invalid.hashCode());

	}

	private void setName(final ProtocolParameterPrimaryKeyImpl pk, final String name) {
		ReflectionTestUtils.setField(pk, "name", name);
	}

	private void setProtocoll(final ProtocolParameterPrimaryKeyImpl pk, final String protocol) {
		ReflectionTestUtils.setField(pk, "protocol", protocol);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void equals() throws InterruptedException {
		final var protocolParameterPrimaryKey = new ProtocolParameterPrimaryKeyImpl(name, protocol);

		final var other = new ProtocolParameterPrimaryKeyImpl(name, protocol);

		assertTrue(protocolParameterPrimaryKey.equals(other));

		setProtocoll(other, RandomTestUtil.randomString());
		assertFalse(protocolParameterPrimaryKey.equals(other));

		setProtocoll(other, protocol);
		setName(other, RandomTestUtil.randomString());
		assertFalse(protocolParameterPrimaryKey.equals(other));

		setProtocoll(other, RandomTestUtil.randomString());
		assertFalse(protocolParameterPrimaryKey.equals(other));

		final ProtocolParameterPrimaryKeyImpl otherProtocolParameterPrimaryKey = new ProtocolParameterPrimaryKeyImpl();
		assertFalse(protocolParameterPrimaryKey.equals(otherProtocolParameterPrimaryKey));
		assertFalse(otherProtocolParameterPrimaryKey.equals(protocolParameterPrimaryKey));
		assertFalse(otherProtocolParameterPrimaryKey.equals(new ProtocolParameterPrimaryKeyImpl()));
		assertTrue(otherProtocolParameterPrimaryKey.equals(otherProtocolParameterPrimaryKey));
		assertFalse(protocolParameterPrimaryKey.equals(new String()));

	}

}
