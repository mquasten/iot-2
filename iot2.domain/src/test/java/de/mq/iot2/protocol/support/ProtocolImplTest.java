package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.support.ProtocolImpl.MESSAGE_EXECUTION_TIME_REQUIRED;
import static de.mq.iot2.protocol.support.ProtocolImpl.MESSAGE_NAME_IS_REQUIRED;
import static de.mq.iot2.protocol.support.ProtocolImpl.MESSAGE_STATUS_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.Protocol.Status;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;
import jakarta.persistence.Id;

class ProtocolImplTest {
	private final String name = RandomTestUtil.randomString();

	@Test
	void create() {
		final Protocol protocol = new ProtocolImpl(name);
		assertTrue(Duration.between(protocol.executionTime(), LocalDateTime.now()).getSeconds() <= 1);
		assertEquals(name, protocol.name());
		assertEquals(Status.Started, protocol.status());
		final var ids = new ArrayList<>();
		ReflectionUtils.doWithFields(ProtocolImpl.class, field -> ids.add(ReflectionTestUtils.getField(protocol, field.getName()).toString()), field -> field.isAnnotationPresent(Id.class));
		final var id = DataAccessUtils.requiredSingleResult(ids);
		assertEquals(IdUtil.id(protocol.executionTime().atZone(ZoneId.systemDefault()).toEpochSecond(), name), id);
		assertEquals(Optional.empty(), protocol.logMessage());
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { "", " ", "\t" })
	void createEmptyName(final String value) {
		assertEquals(MESSAGE_NAME_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> new ProtocolImpl(value)).getMessage());
	}

	@Test
	void createWithoutArguments() {
		Protocol protocol = BeanUtils.instantiateClass(ProtocolImpl.class);
		assertEquals(MESSAGE_NAME_IS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> protocol.name()).getMessage());
		assertEquals(MESSAGE_STATUS_REQUIRED, assertThrows(IllegalArgumentException.class, () -> protocol.status()).getMessage());
		assertEquals(MESSAGE_EXECUTION_TIME_REQUIRED, assertThrows(IllegalArgumentException.class, () -> protocol.executionTime()).getMessage());
	}

	@Test
	void assignErrorState() {
		final Protocol protocol = new ProtocolImpl(name);
		assertEquals(Status.Started, protocol.status());

		protocol.assignErrorState();

		assertEquals(Status.Error, protocol.status());

		assertThrows(IllegalArgumentException.class, () -> protocol.assignErrorState());
		assertThrows(IllegalArgumentException.class, () -> protocol.assignSuccessState());
	}

	@Test
	void assignSuccessState() {
		final Protocol protocol = new ProtocolImpl(name);
		assertEquals(Status.Started, protocol.status());

		protocol.assignSuccessState();

		assertEquals(Status.Success, protocol.status());

		assertThrows(IllegalArgumentException.class, () -> protocol.assignSuccessState());
		assertThrows(IllegalArgumentException.class, () -> protocol.assignErrorState());
	}

	@Test
	void assignLogMessage() {
		final Protocol protocol = new ProtocolImpl(name);
		assertEquals(Optional.empty(), protocol.logMessage());

		final var logMessage = RandomTestUtil.randomString();
		protocol.assignLogMessage(logMessage);

		assertEquals(Optional.of(logMessage), protocol.logMessage());
	}
	
	@Test
	void hash() {
		final Protocol protocol = new ProtocolImpl(name);
		assertEquals(protocol.name().hashCode() + protocol.executionTime().hashCode(), protocol.hashCode());
	}
	
	@Test
	void hashCodeEmtyFields() {
		final Protocol protocol = BeanUtils.instantiateClass(ProtocolImpl.class);
		assertEquals(System.identityHashCode(protocol), protocol.hashCode());
		setName(protocol, name);
		assertEquals(System.identityHashCode(protocol), protocol.hashCode());
	}
	
	@Test
	void equals() throws InterruptedException {
		final Protocol protocol = new ProtocolImpl(name);
		Thread.sleep(10);
		final Protocol other = new ProtocolImpl(name);
		
		
		assertFalse(protocol.equals(other));
		
		setTime(other, protocol.executionTime());
		assertTrue(protocol.equals(other));
		
		setName(other, RandomTestUtil.randomString());
		assertFalse(protocol.equals(other));
		
		final Protocol otherProtocol = BeanUtils.instantiateClass(ProtocolImpl.class);
	
		assertFalse(protocol.equals(otherProtocol));
		assertFalse(otherProtocol.equals(protocol));
		assertFalse(otherProtocol.equals(BeanUtils.instantiateClass(ProtocolImpl.class)));
		assertTrue(otherProtocol.equals(otherProtocol));
		assertFalse(protocol.equals(new String()));
		
		
	}

	private void setTime(final Protocol protocol, LocalDateTime localDateTime) {
		ReflectionTestUtils.setField(protocol, "executionTime", localDateTime);
	}
	
	private void setName(final Protocol protocol, String name) {
		ReflectionTestUtils.setField(protocol, "name", name);
	}

}
