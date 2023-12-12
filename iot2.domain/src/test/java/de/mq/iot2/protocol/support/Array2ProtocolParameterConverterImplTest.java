package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.util.Pair;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus;
import de.mq.iot2.support.IdUtil;

class Array2ProtocolParameterConverterImplTest {
	
	private static final SystemvariableStatus SYSTEM_VARIABLE_STATUS = SystemvariableStatus.Updated;
	private static final String LOGMESSAGE = "logmessage";
	private static final Protocol.Status PROTOCOL_STATUS = Protocol.Status.Success;
	private static final long EXECUTION_TIME = 1700851687261L;
	private static final String PROTOCOL_NAME = "end-of-day";
	private static final String PARAMETER_VALUE = "Freizeit";
	private static final ProtocolParameterType PARAMETER_TYPE = ProtocolParameterType.RulesEngineArgument;
	private static final String PARAMETER_NAME = "Cycle";
	private static final String PARAMETER_CLASS = "Parameter";
	
	private static final String SYSTEM_VARIABLE_PARAMETER_CLASS = "Systemvariable";
	private static final String PROTOCOL_ID = UUID.randomUUID().toString();
	
	private final Converter<Pair<String[], Map<String, Protocol> >, ProtocolParameter> converter = new Array2ProtocolParameterConverterImpl(new DefaultConversionService());
	
	
	@Test
	void protocolParameterProtocolExists() {
		final String[] columns = {PARAMETER_CLASS, PARAMETER_NAME ,PARAMETER_TYPE.name() , PARAMETER_VALUE , PROTOCOL_ID, "", "" , "", ""};
		
		final Protocol protocol = Mockito.mock(ProtocolImpl.class);
		IdUtil.assignId(protocol, PROTOCOL_ID);
		
		
	
		final ProtocolParameter result =  converter.convert(Pair.of(columns, Map.of(PROTOCOL_ID , protocol)));
		
		assertEquals(protocol, result.protocol());
		assertEquals(PARAMETER_NAME, result.name());
		assertEquals(PARAMETER_TYPE, result.type());
		assertEquals(PARAMETER_VALUE, result.value());
		
		assertTrue(result instanceof ProtocolParameterImpl);
		assertFalse(result instanceof SystemvariableProtocolParameterImpl);
		
	}
	
	@Test
	void protocolParameter() {
		final String[] columns = {PARAMETER_CLASS, PARAMETER_NAME ,PARAMETER_TYPE.name() , PARAMETER_VALUE ,  PROTOCOL_ID, PROTOCOL_NAME, ""+EXECUTION_TIME,PROTOCOL_STATUS.name() , ""};
	
		final ProtocolParameter result =  converter.convert(Pair.of(columns, Map.of()));
		
		//assertEquals(protocol, result.protocol());
		assertEquals(PARAMETER_NAME, result.name());
		assertEquals(PARAMETER_TYPE, result.type());
		assertEquals(PARAMETER_VALUE, result.value());
		
		assertEquals(PROTOCOL_NAME, result.protocol().name());
		assertEquals(PROTOCOL_STATUS, result.protocol().status());
		assertEquals(PROTOCOL_ID, IdUtil.getId(result.protocol()));
		
		assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(EXECUTION_TIME), ZoneId.systemDefault()), result.protocol().executionTime());
		
		assertTrue(result instanceof ProtocolParameterImpl);
		assertFalse(result instanceof SystemvariableProtocolParameterImpl);
		assertTrue(result.protocol().logMessage().isEmpty());
		
	}
	
	@Test
	void systemVariableProtocolExists() {
		final String[] columns = {SYSTEM_VARIABLE_PARAMETER_CLASS, PARAMETER_NAME ,SYSTEM_VARIABLE_STATUS.name() , PARAMETER_VALUE , PROTOCOL_ID, "", "","" , ""};
		
		final Protocol protocol = Mockito.mock(ProtocolImpl.class);
		IdUtil.assignId(protocol, PROTOCOL_ID);
		
		
	
		final ProtocolParameter result =   converter.convert(Pair.of(columns, Map.of(PROTOCOL_ID , protocol)));
		
		
		assertEquals(protocol, result.protocol());
		assertEquals(PARAMETER_NAME, result.name());
		assertEquals(ProtocolParameterType.Result, result.type());
		assertEquals(PARAMETER_VALUE, result.value());
		
		assertTrue(result instanceof SystemvariableProtocolParameterImpl);
		assertEquals(SYSTEM_VARIABLE_STATUS,(( SystemvariableProtocolParameter) result).status());
		
	}
	
	@Test
	void systemVariableParameter() {
		final String[] columns = {SYSTEM_VARIABLE_PARAMETER_CLASS, PARAMETER_NAME ,SYSTEM_VARIABLE_STATUS.name() , PARAMETER_VALUE ,  PROTOCOL_ID, PROTOCOL_NAME, ""+EXECUTION_TIME,PROTOCOL_STATUS.name() , LOGMESSAGE};
	
		final ProtocolParameter result =  converter.convert(Pair.of(columns, Map.of()));
		
		assertEquals(PARAMETER_NAME, result.name());
		assertEquals(ProtocolParameterType.Result, result.type());
		assertEquals(PARAMETER_VALUE, result.value());
		
		assertEquals(PROTOCOL_NAME, result.protocol().name());
		assertEquals(PROTOCOL_STATUS, result.protocol().status());
		assertEquals(PROTOCOL_ID, IdUtil.getId(result.protocol()));
		
		assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(EXECUTION_TIME), ZoneId.systemDefault()), result.protocol().executionTime());
		
		assertTrue(result instanceof SystemvariableProtocolParameterImpl);
		
		assertEquals(SYSTEM_VARIABLE_STATUS,(( SystemvariableProtocolParameter) result).status());
		
		assertEquals(Optional.of(LOGMESSAGE), result.protocol().logMessage());
		
	}
	
	@Test
	void protocolParameterWrongNumberOfColumns() {
		final String[] columns = {PARAMETER_CLASS, PARAMETER_NAME ,PARAMETER_TYPE.name() , PARAMETER_VALUE ,  PROTOCOL_ID, PROTOCOL_NAME, ""+EXECUTION_TIME,PROTOCOL_STATUS.name() };
	
		assertEquals(Array2ProtocolParameterConverterImpl.WRONG_NUMBER_OF_COLUMNS_MESSAGE,assertThrows(IllegalArgumentException.class,  () ->converter.convert(Pair.of(columns, Map.of()))).getMessage());
	}
		

}
