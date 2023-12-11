package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;


import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.support.IdUtil;

class ProtocolParameterCsvConverterImplTest {
	private static final String CSV_DELIMITER = ";";

	private final Converter<Pair<ProtocolParameter, Boolean>, String[]> csvConverter = new ProtocolParameterCsvConverterImpl(CSV_DELIMITER);

	private final Protocol protocol = new ProtocolImpl("protocolName");

	final ProtocolParameter protocolParameter = new ProtocolParameterImpl(protocol, "parameterName", ProtocolParameterType.Input, "value");
	
	@BeforeEach
	void setp() {
		protocol.assignLogMessage("logMessage");	
	}
	
	@Test
	void protocolParametrer() {
		
		final String[] results = (csvConverter.convert(Pair.of(protocolParameter, false)));
		
		assertEquals(9, results.length);
		assertEquals(ProtocolParameterImpl.DISCRIMINATOR_VALUE, results[0]);
		assertEquals(protocolParameter.name(), results[1]);
		assertEquals(protocolParameter.type().toString(), results[2]);
		assertEquals(protocolParameter.value(), results[3]);
		assertEquals(IdUtil.getId(protocol), results[4]);
		assertEquals(protocol.name(), results[5]);
		assertEquals("" + ZonedDateTime.of(protocol.executionTime(), ZoneId.systemDefault()).toInstant().toEpochMilli(), results[6]);
		assertEquals("" + protocol.status(), results[7]);
		assertEquals(protocol.logMessage().get(), results[8]);
		
	}
	
	@Test
	void protocolParametrerProcessed() {
		final String[] results = (csvConverter.convert(Pair.of(protocolParameter, true)));
		
		assertEquals(9, results.length);
		assertEquals(ProtocolParameterImpl.DISCRIMINATOR_VALUE, results[0]);
		assertEquals(protocolParameter.name(), results[1]);
		assertEquals(protocolParameter.type().toString(), results[2]);
		assertEquals(protocolParameter.value(), results[3]);
		assertEquals(IdUtil.getId(protocol), results[4]);
		assertTrue(results[5].isEmpty());
		assertTrue( results[6].isEmpty());
		assertTrue(results[7].isEmpty());
		assertTrue(results[8].isEmpty());
		
	}

}
