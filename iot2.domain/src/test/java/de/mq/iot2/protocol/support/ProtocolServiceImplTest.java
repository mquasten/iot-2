package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.Protocol.Status.Error;
import static de.mq.iot2.protocol.Protocol.Status.Started;
import static de.mq.iot2.protocol.Protocol.Status.Success;
import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus.Calculated;
import static de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus.Updated;
import static de.mq.iot2.protocol.support.ProtocolServiceImpl.MESSAGE_DAYS_BACK_INVALID;
import static de.mq.iot2.protocol.support.ProtocolServiceImpl.MESSAGE_PROTOCOL_NOT_FOUND_FOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;

import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;
import de.mq.iot2.sysvars.SystemVariable;

class ProtocolServiceImplTest {
	private static final String EMPTY_OPTIONAL_STRING = "<Empty>";
	private final ProtocolRepository protocolRepository = mock(ProtocolRepository.class);
	private final ProtocolParameterRepository protocolParameterRepository = mock(ProtocolParameterRepository.class);
	private final ConversionService conversionService = mock(ConversionService.class);
	private final ProtocolService protocolService = new ProtocolServiceImpl(protocolRepository, protocolParameterRepository, conversionService);

	private final Protocol protocol =  new ProtocolImpl(RandomTestUtil.randomString());
	private static final LocalDate MAXWEELS_BIRTHDATE = LocalDate.of(1831, 6, 18);
	private final Collection<ProtocolParameter> savedParameters = new ArrayList<>();

	@BeforeEach
	void beforeEach() {
		Mockito.when(conversionService.canConvert(Mockito.any(Class.class), Mockito.any(Class.class))).thenReturn(true);
		doAnswer(answer -> convert(answer.getArgument(0))).when(conversionService).convert(Mockito.any(), Mockito.any());
		doAnswer(answer -> addParameter(savedParameters, answer.getArgument(0, ProtocolParameter.class))).when(protocolParameterRepository).save(Mockito.any(ProtocolParameter.class));
	
	}

	@Test
	void protocol() throws InterruptedException {
		final var name = RandomTestUtil.randomString();
		doAnswer(answer -> answer.getArgument(0, Protocol.class)).when(protocolRepository).save(any(Protocol.class));

		final Protocol protocol = protocolService.protocol(name);

		assertEquals(name, protocol.name());
		assertEquals(Started, protocol.status());
		assertTrue(Duration.between(protocol.executionTime(), LocalDateTime.now()).getSeconds() < 1);

	}
	
	@Test
	void save() {
		protocolService.save(protocol);
		
		verify(protocolRepository).save(protocol);
	}
	
	@Test
	void success() {
		final var message = RandomTestUtil.randomString();
		protocolService.success(protocol, message);
		
		assertEquals(Optional.of(message) ,protocol.logMessage());
		assertEquals(Success, protocol.status());
		verify(protocolRepository).save(protocol);
	}
	
	@Test
	void successWithoutMessage() {
		protocolService.success(protocol);
		
		assertEquals(Optional.empty() ,protocol.logMessage());
		assertEquals(Success, protocol.status());
		verify(protocolRepository).save(protocol);
	}
	
	@Test
	void error() {
		final var throwable = new IllegalStateException(RandomTestUtil.randomString());
		protocolService.error(protocol, throwable);
		final var stringwriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringwriter));
		
		assertEquals(Optional.of(stringwriter.toString()) ,protocol.logMessage());
		assertEquals(Error, protocol.status());
		verify(protocolRepository).save(protocol);
	}

	@Test
	void assignParameterConfiguration() {
		final Map<? extends Enum<?>, Object> parameters = Map.of(Key.MaxSunDownTime, LocalTime.of(17, 0), Key.SunUpDownType, TwilightType.Mathematical, Key.ShadowTemperature, Double.valueOf(8.08), Key.DaysBack, Integer.valueOf(30));

		protocolService.assignParameter(protocol, ProtocolParameterType.Configuration, parameters);

		assertEquals(parameters.size(), savedParameters.size());
		savedParameters.forEach(parameter -> checkParameterConfiguration(parameters, parameter));
		verify(protocolParameterRepository, Mockito.times(parameters.size())).save(Mockito.any());
	}

	@Test
	void assignParameterRulesEngineArgument() {
		final String cycleName = RandomTestUtil.randomString();
		final Cycle cycle = mock(Cycle.class);
		when(cycle.name()).thenReturn(cycleName);
		final Map<? extends Enum<?>, Object> arguments = Map.of(TestEndOfDayArguments.Date, MAXWEELS_BIRTHDATE, TestEndOfDayArguments.SunUpTime, LocalTime.of(7, 30), TestEndOfDayArguments.Cycle, cycle, TestEndOfDayArguments.MaxForecastTemperature,
				Optional.of(11.11), TestEndOfDayArguments.UpdateTime, Optional.of(LocalTime.of(22, 30)));

		protocolService.assignParameter(protocol, ProtocolParameterType.RulesEngineArgument, arguments);

		assertEquals(arguments.size(), savedParameters.size());

		savedParameters.forEach(parameter -> checkParameterRulesEngineArgument(arguments, parameter, cycleName));

		verify(protocolParameterRepository, Mockito.times(arguments.size())).save(Mockito.any());
	}

	@Test
	void assignParameterRulesEngineArgumentOptionalEmtpy() {
		final String cycleName = RandomTestUtil.randomString();
		final Cycle cycle = mock(Cycle.class);
		when(cycle.name()).thenReturn(cycleName);
		final Map<? extends Enum<?>, Object> arguments = Map.of(TestEndOfDayArguments.MaxForecastTemperature, Optional.empty(), TestEndOfDayArguments.UpdateTime, Optional.empty());

		protocolService.assignParameter(protocol, ProtocolParameterType.RulesEngineArgument, arguments);

		assertEquals(arguments.size(), savedParameters.size());

		savedParameters.forEach(parameter -> assertEquals(EMPTY_OPTIONAL_STRING, parameter.value()));

		verify(protocolParameterRepository, Mockito.times(arguments.size())).save(Mockito.any());
	}

	private void checkParameterConfiguration(final Map<? extends Enum<?>, Object> parameters, final ProtocolParameter parameter) {
		assertEquals(parameters.get(Key.valueOf(parameter.name())).toString(), parameter.value());
		assertEquals(protocol, parameter.protocol());
		assertEquals(ProtocolParameterType.Configuration, parameter.type());
	}

	private void checkParameterRulesEngineArgument(final Map<? extends Enum<?>, Object> parameters, final ProtocolParameter parameter, final String cycleName) {
		if (TestEndOfDayArguments.valueOf(parameter.name()) == TestEndOfDayArguments.Cycle) {
			assertEquals(cycleName, parameter.value());
		} else {
			checkValue(parameters, parameter);
		}
		assertEquals(protocol, parameter.protocol());
		assertEquals(ProtocolParameterType.RulesEngineArgument, parameter.type());
	}

	private void checkValue(final Map<? extends Enum<?>, Object> parameters, final ProtocolParameter parameter) {
		if (parameters.get(TestEndOfDayArguments.valueOf(parameter.name())) instanceof Optional) {

			assertEquals(((Optional<?>) parameters.get(TestEndOfDayArguments.valueOf(parameter.name()))).get().toString(), parameter.value());
		} else {

			assertEquals(parameters.get(TestEndOfDayArguments.valueOf(parameter.name())).toString(), parameter.value());
		}
	}

	private ProtocolParameter addParameter(final Collection<ProtocolParameter> savedParameters, final ProtocolParameter parameter) {
		savedParameters.add(parameter);
		return parameter;
	}

	@SuppressWarnings("unchecked")
	private String convert(final Object value) {
		if (value instanceof TwilightType) {
			return ((TwilightType) value).name();
		}

		if (value instanceof Key) {
			return ((Key) value).name();

		}

		if (value instanceof LocalTime) {
			return ((LocalTime) value).format(DateTimeFormatter.ofPattern("HH:mm"));
		}

		if (value instanceof Cycle) {
			return ((Cycle) value).name();
		}

		if (value instanceof Optional) {
			return ((Optional<Object>) value).orElse(EMPTY_OPTIONAL_STRING).toString();
		}

		return value.toString();

	}
	
	
	@Test
	void assignSystemvariableParameter() {
		
		final Collection<SystemVariable> systemVariables = List.of(new SystemVariable(RandomTestUtil.randomString(), RandomTestUtil.randomString()), new SystemVariable(RandomTestUtil.randomString(), RandomTestUtil.randomString()));
		protocolService.assignParameter(protocol, systemVariables);
		
		assertEquals(2, savedParameters.size());
		
		final Map<String, String> expected = systemVariables.stream().collect(Collectors.toMap(SystemVariable::getName, SystemVariable::getValue));
		
		savedParameters.forEach(parameter -> checkSystemParameter(expected, parameter));
		
	}

	private void checkSystemParameter(final Map<String, String> expected, final ProtocolParameter parameter) {
		assertTrue(expected.keySet().contains(parameter.name()));
		assertEquals(expected.get(parameter.name()), parameter.value());
		assertEquals(Calculated, ((SystemvariableProtocolParameter) parameter).status());
		assertEquals(protocol, parameter.protocol());
	}

	
	@Test
	void updateSystemVariables() {
		final SystemVariable lastBatchUpdate = new SystemVariable("LastBatchUpdate", LocalTime.now().toString());
		final SystemVariable month = new SystemVariable("Month", LocalDate.now().getMonth().toString());
		final SystemvariableProtocolParameter lastBatchUpdateParameter = new SystemvariableProtocolParameterImpl(protocol, lastBatchUpdate.getName(), lastBatchUpdate.getValue());
		
		when(protocolParameterRepository.findByProtocolIdNameNameIn(IdUtil.getId(protocol), List.of(lastBatchUpdate.getName(), month.getName() ))).thenReturn(List.of(lastBatchUpdateParameter));
		
		protocolService.updateSystemVariables(protocol, List.of(lastBatchUpdate,month));
		
		assertEquals(Updated,lastBatchUpdateParameter.status());
		verify(protocolParameterRepository).save(lastBatchUpdateParameter);
	}
	
	@Test
	void assignParameter() {
		final var name = RandomTestUtil.randomString();
		final var value = RandomTestUtil.randomLong();
		protocolService.assignParameter(protocol, Result, name, value);
		
		assertEquals(1, savedParameters.size());
		final ProtocolParameter protocolParameter = savedParameters.iterator().next();
		assertEquals(name, protocolParameter.name());
		assertEquals(String.valueOf(value), protocolParameter.value());
		assertEquals(Result, protocolParameter.type());
	}
	
	@Test
	void deleteProtocols() {
		final var daysBack = 30;
		final var  expectedDeletionDateTime =   LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(daysBack);
		final Protocol protocol01 = mock(Protocol.class);
		final Protocol protocol02 = mock(Protocol.class);
		final ProtocolParameter protocolParameter01 = mock(ProtocolParameter.class);
		final ProtocolParameter protocolParameter02 = mock(ProtocolParameter.class);
		
		when(protocolRepository.findByExecutionTimeBefore(argThat(dateTime ->  expectedDeletionDateTime.equals(dateTime) ) )).thenReturn(List.of(protocol01, protocol02));
		when(protocolParameterRepository.findByProtocol(protocol01)).thenReturn(List.of(protocolParameter01));
		when(protocolParameterRepository.findByProtocol(protocol02)).thenReturn(List.of(protocolParameter02));
		
		protocolService.deleteProtocols(daysBack);
		
		verify(protocolRepository).delete(protocol01);
		verify(protocolRepository).delete(protocol02);
		verify(protocolParameterRepository).delete(protocolParameter01);
		verify(protocolParameterRepository).delete(protocolParameter02);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0,-1})
	void deleteProtocolsInvalidNumberOfDays(final int value) {
		assertEquals(MESSAGE_DAYS_BACK_INVALID, assertThrows(IllegalArgumentException.class, () -> protocolService.deleteProtocols(0)).getMessage());
	}
	
	@Test
	void protocolNames() {
		final Collection<String> protocolNames = List.of("end-of-day", "cleanup-calendar", "cleanup-protocol");
		when(protocolRepository.findDistinctNames()).thenReturn(protocolNames);
		assertEquals(protocolNames, protocolService.protocolNames());
		
		verify(protocolRepository).findDistinctNames();
	}
	
	
	@Test
	void protocols() {
		final var protocolName = RandomTestUtil.randomString();
		final Collection<Protocol> protocols = List.of(Mockito.mock(Protocol.class), Mockito.mock(Protocol.class));
		when(protocolRepository.findByNameOrderByExecutionTime(protocolName)).thenReturn(protocols);
		
		assertEquals(protocols, protocolService.protocols(protocolName));
		
		verify(protocolRepository).findByNameOrderByExecutionTime(protocolName);
	}
	
	@Test
	void protocolById() {
		final var id = RandomTestUtil.randomString();
		final Protocol protocol = Mockito.mock(Protocol.class);
		when(protocolRepository.findById(id)).thenReturn(Optional.of(protocol));
		
		assertEquals(protocol, protocolService.protocolById(id));
		
		verify(protocolRepository).findById(id);
	}
	
	@Test
	void protocolByIdNotFound() {
		final var id = UUID.randomUUID().toString();

		assertEquals(String.format(MESSAGE_PROTOCOL_NOT_FOUND_FOR_ID , id), assertThrows(EmptyResultDataAccessException.class, () -> protocolService.protocolById(id)).getMessage());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {""," " , "\t"})
	@NullSource
	void protocolByIdIdEmpty(final String id) {
		assertEquals(ProtocolServiceImpl.MESSAGE_ID_REQUIRED, assertThrows(IllegalArgumentException.class, () -> protocolService.protocolById(id)).getMessage());
	}
	
	@Test
	void protocolParameters() {
		final var id = UUID.randomUUID().toString();
		when(protocolRepository.findById(id)).thenReturn(Optional.of(protocol));
		final Collection<ProtocolParameter> protocolParameters = List.of(mock(ProtocolParameter.class), mock(ProtocolParameter.class));
		when(protocolParameterRepository.findByProtocol(protocol)).thenReturn(protocolParameters);
		
		assertEquals(protocolParameters,protocolService.protocolParameters(id));
		
		verify(protocolRepository).findById(id);
		verify(protocolParameterRepository).findByProtocol(protocol);
	}
}

enum TestEndOfDayArguments {
	Date, SunUpTime, Cycle, MaxForecastTemperature, UpdateTime;

}