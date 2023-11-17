package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.rules.RuleService;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;
import de.mq.iot2.weather.WeatherService;

class EndOfDayBatchImplTest {

	private final CalendarService calendarService = Mockito.mock(CalendarService.class);
	private final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private final RuleService ruleService = Mockito.mock(RuleService.class);
	private final SystemVariableService systemVariableService = Mockito.mock(SystemVariableService.class);
	private final WeatherService weatherService = Mockito.mock(WeatherService.class);
	private final ProtocolService protocolService = Mockito.mock(ProtocolService.class);
	private final EndOfDayBatchImpl endOfDayBatch = new EndOfDayBatchImpl(calendarService, configurationService, ruleService, systemVariableService, weatherService, protocolService);

	private final Protocol protocol = Mockito.mock(Protocol.class);
	private final LocalDate date = LocalDate.now().plusDays(1);

	private final Cycle cycle = Mockito.mock(Cycle.class);

	@Test
	final void execute() {
		final var sunUpTime = LocalTime.of(8, 0);
		final var sunDownTime = LocalTime.of(17, 0);
		final var maxForecastTemperature = Optional.of(11.11d);

		Mockito.when(calendarService.cycle(date)).thenReturn(cycle);
		final Map<Key, Object> parameters = Map.of(Key.SunUpDownType, TwilightType.Civil);

		Mockito.when(configurationService.parameters(RuleKey.EndOfDay, cycle)).thenReturn(parameters);
		Mockito.when(calendarService.sunUpTime(date, TwilightType.Civil)).thenReturn(Optional.of(sunUpTime));
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Civil)).thenReturn(Optional.of(sunDownTime));
		Mockito.when(weatherService.maxForecastTemperature(date)).thenReturn(maxForecastTemperature);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<Key, Object>> parameterCapture = ArgumentCaptor.forClass(Map.class);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<? extends Enum<?>, Object>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
		final var systemVariables = List.of(new SystemVariable());
		Mockito.when(protocolService.protocol(EndOfDayBatchImpl.END_OF_DAY_BATCH_NAME)).thenReturn(protocol);
		//Mockito.when(protocolService.create(EndOfDayBatchImpl.END_OF_DAY_BATCH_NAME)).thenReturn(protocol);
		Mockito.when(systemVariableService.update(systemVariables)).thenReturn(systemVariables);

		Mockito.when(ruleService.process(parameterCapture.capture(), argumentCaptor.capture())).thenReturn(Map.of(EndOfDayArguments.SystemVariables.name(), systemVariables));

		endOfDayBatch.execute(date);

		assertEquals(parameters, parameterCapture.getValue());
		assertEquals(date, argumentCaptor.getValue().get(EndOfDayArguments.Date));
		assertEquals(Optional.of(sunUpTime), argumentCaptor.getValue().get(EndOfDayArguments.SunUpTime));
		assertEquals(Optional.of(sunDownTime), argumentCaptor.getValue().get(EndOfDayArguments.SunDownTime));
		assertEquals(cycle, argumentCaptor.getValue().get(EndOfDayArguments.Cycle));
		assertEquals(maxForecastTemperature, argumentCaptor.getValue().get(EndOfDayArguments.MaxForecastTemperature));
		assertEquals(Optional.empty(), argumentCaptor.getValue().get(EndOfDayArguments.UpdateTime));

		Mockito.verify(systemVariableService).update(systemVariables);

		Mockito.verify(protocolService).save(protocol);
		Mockito.verify(protocolService).assignParameter(protocol, ProtocolParameterType.Configuration, parameters);
		Mockito.verify(protocolService).assignParameter(protocol, ProtocolParameterType.RulesEngineArgument, argumentCaptor.getValue());
		Mockito.verify(protocolService).assignParameter(protocol, systemVariables);
		Mockito.verify(protocolService).updateSystemVariables(protocol, systemVariables);

	}

	@Test
	final void executeDefaultTwilightType() {
		final var sunUpTime = LocalTime.of(8, 0);
		final var sunDownTime = LocalTime.of(17, 0);
		Mockito.when(calendarService.cycle(date)).thenReturn(cycle);
		final Map<Key, Object> parameters = Map.of(Key.MaxSunDownTime, LocalTime.of(23, 30));
		Mockito.when(configurationService.parameters(RuleKey.EndOfDay, cycle)).thenReturn(parameters);
		Mockito.when(calendarService.sunUpTime(date, TwilightType.Mathematical)).thenReturn(Optional.of(sunUpTime));
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Mathematical)).thenReturn(Optional.of(sunDownTime));
		final var systemVariables = List.of(new SystemVariable());
		Mockito.when(ruleService.process(Mockito.anyMap(), Mockito.anyMap())).thenReturn(Map.of(EndOfDayArguments.SystemVariables.name(), systemVariables));

		endOfDayBatch.execute(date);

		Mockito.verify(systemVariableService).update(systemVariables);
	}

	@Test
	final void executeUpdate() {
		final var date = LocalDate.now();
		final var time = LocalTime.of(11, 11);
		final var sunUpTime = LocalTime.of(8, 0);
		final var sunDownTime = LocalTime.of(17, 0);
		final var maxForecastTemperature = Optional.of(11.11d);

		Mockito.when(calendarService.cycle(date)).thenReturn(cycle);
		final Map<Key, Object> parameters = Map.of(Key.SunUpDownType, TwilightType.Civil);

		Mockito.when(configurationService.parameters(RuleKey.EndOfDay, cycle)).thenReturn(parameters);
		Mockito.when(calendarService.sunUpTime(date, TwilightType.Civil)).thenReturn(Optional.of(sunUpTime));
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Civil)).thenReturn(Optional.of(sunDownTime));
		Mockito.when(weatherService.maxForecastTemperature(date)).thenReturn(maxForecastTemperature);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<Key, Object>> parameterCapture = ArgumentCaptor.forClass(Map.class);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<? extends Enum<?>, Object>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
		final var systemVariables = List.of(new SystemVariable());
		Mockito.when(ruleService.process(parameterCapture.capture(), argumentCaptor.capture())).thenReturn(Map.of(EndOfDayArguments.SystemVariables.name(), systemVariables));

		Mockito.when(protocolService.protocol(EndOfDayBatchImpl.END_OF_DAY_UPDATE_BATCH_NAME)).thenReturn(protocol);
		Mockito.when(systemVariableService.update(systemVariables)).thenReturn(systemVariables);

		endOfDayBatch.executeUpdate(time);

		assertEquals(parameters, parameterCapture.getValue());
		assertEquals(date, argumentCaptor.getValue().get(EndOfDayArguments.Date));
		assertEquals(Optional.of(sunUpTime), argumentCaptor.getValue().get(EndOfDayArguments.SunUpTime));
		assertEquals(Optional.of(sunDownTime), argumentCaptor.getValue().get(EndOfDayArguments.SunDownTime));
		assertEquals(cycle, argumentCaptor.getValue().get(EndOfDayArguments.Cycle));
		assertEquals(maxForecastTemperature, argumentCaptor.getValue().get(EndOfDayArguments.MaxForecastTemperature));
		assertEquals(Optional.of(time), argumentCaptor.getValue().get(EndOfDayArguments.UpdateTime));

		Mockito.verify(systemVariableService).update(systemVariables);

		Mockito.verify(protocolService).save(protocol);
		Mockito.verify(protocolService).assignParameter(protocol, ProtocolParameterType.Configuration, parameters);
		Mockito.verify(protocolService).assignParameter(protocol, ProtocolParameterType.RulesEngineArgument, argumentCaptor.getValue());
		Mockito.verify(protocolService).assignParameter(protocol, systemVariables);
		Mockito.verify(protocolService).updateSystemVariables(protocol, systemVariables);
	}

	@Test
	void executeWithException() {
		final var sunUpTime = LocalTime.of(8, 0);
		final var sunDownTime = LocalTime.of(17, 0);
		Mockito.when(calendarService.cycle(date)).thenReturn(cycle);
		Mockito.when(configurationService.parameters(RuleKey.EndOfDay, cycle)).thenReturn(Map.of());
		Mockito.when(calendarService.sunUpTime(date, TwilightType.Mathematical)).thenReturn(Optional.of(sunUpTime));
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Mathematical)).thenReturn(Optional.of(sunDownTime));
		Mockito.when(protocolService.protocol(EndOfDayBatchImpl.END_OF_DAY_BATCH_NAME)).thenReturn(protocol);

		final Throwable exception = new IllegalStateException("message");
		Mockito.when(ruleService.process(Mockito.anyMap(), Mockito.anyMap())).thenThrow(exception);

		assertEquals(exception.getMessage(), assertThrows(IllegalStateException.class, () -> endOfDayBatch.execute(date)).getMessage());

		Mockito.verify(protocolService).error(protocol, exception);
	}

}
