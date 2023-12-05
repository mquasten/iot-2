package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.CollectionUtils;

import de.mq.iot2.protocol.Protocol.Status;


class ProtocolModelTest {
	
	private final ProtocolModel protocolModel = new ProtocolModel();
	
	@Test
	void id() {

		assertNull(protocolModel.getId());
		final var id =  random();
		protocolModel.setId(id);
		assertEquals(id, protocolModel.getId());
	}

	private String random() {
		return RandomStringUtils.random(50);
	}
	
	@Test
	void name() {
		assertNull(protocolModel.getName());
		final var name =  random();
		protocolModel.setName(name);
		assertEquals(name, protocolModel.getName());
	}
	
	@Test
	void executionTime() {
		assertNull(protocolModel.getExecutionTime());
		final var executionTime =  random();
		protocolModel.setExecutionTime(executionTime);
		assertEquals(executionTime, protocolModel.getExecutionTime());
	}
	
	@Test
	void status() {
		assertNull(protocolModel.getStatus());
		final var status =  Status.Success;
		protocolModel.setStatus(status);
		assertEquals(status, protocolModel.getStatus());
	}
	
	@Test
	void protocols() {
		assertTrue(CollectionUtils.isEmpty(protocolModel.getProtocols()));
		final var protocols =  List.of(Mockito.mock(ProtocolModel.class));
		protocolModel.setProtocols(protocols);
		assertEquals(protocols, protocolModel.getProtocols());
	}
	
	@Test
	void logMessage() {
		assertNull(protocolModel.getLogMessage());
		final var logMessage =  random();
		protocolModel.setLogMessage(logMessage);
		assertEquals(logMessage, protocolModel.getLogMessage());
		
	}
	
	@Test
	void logMessageAware() {
		assertFalse(protocolModel.isLogMessageAware());
		protocolModel.setLogMessage(random());
		assertTrue(protocolModel.isLogMessageAware());
	}
	
	@Test
	void logMessageShort() {
		assertNull(protocolModel.getLogMessageShort());
		final var LogMessageFirst = random();
		protocolModel.setLogMessage(LogMessageFirst+random());
		assertEquals(LogMessageFirst+ ProtocolModel.TRUNCATED_POSTFIX, protocolModel.getLogMessageShort());
		protocolModel.setLogMessage(LogMessageFirst);
		assertEquals(LogMessageFirst, protocolModel.getLogMessageShort());
	}

}
