package de.mq.iot2.sysvars.support;

import static de.mq.iot2.sysvars.support.TimerController.TIME_PATTERN;
import static de.mq.iot2.sysvars.support.VariableController.VARIABLE_MODEL_AND_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariableService;
import de.mq.iot2.weather.WeatherService;

class VariableControllerTest {

	private static final double TEMPERATURE = 7.1;
	private static final double TEMPERATURE_NEXT = 7.5;
	private static final LocalTime SUN_DOWNTIME = LocalTime.of(17, 45);
	private static final LocalTime SUN_UPTIME = LocalTime.of(7, 45);

	private static final LocalTime SUN_DOWNTIME_NEXT = LocalTime.of(17, 47);
	private static final LocalTime SUN_UPTIME_NEXT = LocalTime.of(7, 43);

	private final CalendarService calendarService = mock(CalendarService.class);
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	private final WeatherService weatherService = mock(WeatherService.class);
	private final ConversionService conversionService = mock(ConversionService.class);
	private final SystemVariableService systemVariableService = mock(SystemVariableService.class);
	private final Model model = new ExtendedModelMap();
	private VariableController variableController = new VariableController(calendarService, configurationService, weatherService, conversionService, systemVariableService);

	@Test
	void variable() {
		final var date = LocalDate.now();
		when(configurationService.parameter(RuleKey.EndOfDay, Key.SunUpDownType, TwilightType.class)).thenReturn(Optional.of(TwilightType.Civil));
		when(calendarService.sunUpTime(date, TwilightType.Civil)).thenReturn(Optional.of(SUN_UPTIME));
		when(calendarService.sunDownTime(date, TwilightType.Civil)).thenReturn(Optional.of(SUN_DOWNTIME));
		when(calendarService.sunUpTime(date.plusDays(1), TwilightType.Civil)).thenReturn(Optional.of(SUN_UPTIME_NEXT));
		when(calendarService.sunDownTime(date.plusDays(1), TwilightType.Civil)).thenReturn(Optional.of(SUN_DOWNTIME_NEXT));
		when(weatherService.maxForecastTemperature(date)).thenReturn(Optional.of(TEMPERATURE));
		when(weatherService.maxForecastTemperature(date.plusDays(1))).thenReturn(Optional.of(TEMPERATURE_NEXT));
		doAnswer(a -> "" + a.getArgument(0)).when(conversionService).convert(anyDouble(), any());

		assertEquals(VARIABLE_MODEL_AND_VIEW_NAME, variableController.variable(model, false, Locale.ENGLISH));

		final VariableModel variableModel = (VariableModel) model.getAttribute(VARIABLE_MODEL_AND_VIEW_NAME);
		assertEquals(date, variableModel.getDate());
		assertFalse(variableModel.isShowVariables());
		assertEquals(Locale.ENGLISH, ReflectionTestUtils.getField(variableModel, "locale"));
		assertEquals(TwilightType.Civil.name().toLowerCase(), variableModel.getTwilightType());
		assertEquals(format(SUN_UPTIME), variableModel.getSunUpToday());
		assertEquals(format(SUN_UPTIME_NEXT), variableModel.getSunUpTomorrow());
		assertEquals(format(SUN_DOWNTIME), variableModel.getSunDownToday());
		assertEquals(format(SUN_DOWNTIME_NEXT), variableModel.getSunDownTomorrow());
		assertEquals("" + TEMPERATURE, variableModel.getMaxTemperatureToday());
		assertEquals("" + TEMPERATURE_NEXT, variableModel.getMaxTemperatureTomorrow());
		assertEquals(0, variableModel.getVariables().size());

		verify(systemVariableService, never()).read();
	}

	private String format(final LocalTime time) {
		return DateTimeFormatter.ofPattern(TIME_PATTERN).format(time);

	}
}
