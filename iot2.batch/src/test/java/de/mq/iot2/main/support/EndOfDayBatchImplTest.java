package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TimeType;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.rules.RuleService;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;
import de.mq.iot2.weather.support.WeatherService;

class EndOfDayBatchImplTest {

	private final CalendarService calendarService = Mockito.mock(CalendarService.class);
	private final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private final RuleService ruleService = Mockito.mock(RuleService.class);
	private final SystemVariableService systemVariableService = Mockito.mock(SystemVariableService.class);
	private final WeatherService weatherService = Mockito.mock(WeatherService.class);
	private final EndOfDayBatchImpl endOfDayBatch = new EndOfDayBatchImpl(calendarService, configurationService, ruleService, systemVariableService, weatherService);

	private final LocalDate date = LocalDate.now().plusDays(1);

	private final Cycle cycle = Mockito.mock(Cycle.class);

	@Test
	final void execute() {
		final var sunUpTime = LocalTime.of(8, 0);
		final var sunDownTime = LocalTime.of(17, 0);
		final var maxForecastTemperature = Optional.of( 11.11d);;
		Mockito.when(calendarService.cycle(date)).thenReturn(cycle);
		final Map<Key, Object> parameters = Map.of(Key.SunUpDownType, TwilightType.Civil);
		Mockito.when(calendarService.timeType(date)).thenReturn(TimeType.Winter);
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

		endOfDayBatch.execute(date);

		assertEquals(parameters, parameterCapture.getValue());
		assertEquals(TimeType.Winter, argumentCaptor.getValue().get(EndOfDayArguments.TimeType));
		assertEquals(date, argumentCaptor.getValue().get(EndOfDayArguments.Date));
		assertEquals(Optional.of(sunUpTime), argumentCaptor.getValue().get(EndOfDayArguments.SunUpTime));
		assertEquals(Optional.of(sunDownTime), argumentCaptor.getValue().get(EndOfDayArguments.SunDownTime));
		assertEquals(cycle, argumentCaptor.getValue().get(EndOfDayArguments.Cycle));
		assertEquals(maxForecastTemperature, argumentCaptor.getValue().get(EndOfDayArguments.MaxForecastTemperature));

		Mockito.verify(systemVariableService).update(systemVariables);
	}
	
	@Test
	final void executeDefaultTwilightType() {
		final var sunUpTime = LocalTime.of(8, 0);
		final var sunDownTime = LocalTime.of(17, 0);
		Mockito.when(calendarService.cycle(date)).thenReturn(cycle);
		final Map<Key, Object> parameters = Map.of(Key.MaxSunDownTime, LocalTime.of(23, 30));
		Mockito.when(calendarService.timeType(date)).thenReturn(TimeType.Winter);
		Mockito.when(configurationService.parameters(RuleKey.EndOfDay, cycle)).thenReturn(parameters);
		Mockito.when(calendarService.sunUpTime(date, TwilightType.Mathematical)).thenReturn(Optional.of(sunUpTime));
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Mathematical)).thenReturn(Optional.of(sunDownTime));
		final var systemVariables = List.of(new SystemVariable());
		Mockito.when(ruleService.process(Mockito.anyMap(), Mockito.anyMap())).thenReturn(Map.of(EndOfDayArguments.SystemVariables.name(), systemVariables));

		endOfDayBatch.execute(date);
		
		Mockito.verify(systemVariableService).update(systemVariables);
	}
	
}
