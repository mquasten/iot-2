package de.mq.iot2.sysvars.support;

import static de.mq.iot2.sysvars.support.TimerController.TIMER_MODEL_AND_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.core.convert.ConversionService;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;
import de.mq.iot2.weather.WeatherService;

class TimerControllerTest {

	private static final LocalTime SUN_DOWNTIME = LocalTime.of(17, 45);
	private static final LocalTime SHADOW_TIME = LocalTime.of(9, 0);
	private static final LocalTime SUN_UPTIME = LocalTime.of(7, 45);
	private static final LocalTime UPTIME = LocalTime.of(6, 15);
	private final SystemVariableService systemVariableService = mock(SystemVariableService.class);
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	private final CalendarService calendarService = mock(CalendarService.class);
	private final WeatherService weatherService = mock(WeatherService.class);
	private final ConversionService conversionService = mock(ConversionService.class);
	private final TimerController timerController = new TimerController(systemVariableService, configurationService, calendarService, weatherService, conversionService);

	private final Model model = new ExtendedModelMap();

	private final Cycle cycle = mock(Cycle.class);

	private final Map<Key, Object> parameters = new HashMap<>();
	
	private final BindingResult bindingResults = mock(BindingResult.class);

	
	
	@ParameterizedTest
	@ValueSource(booleans = {false, true })
	void variable(final boolean update) {
		prepareVariable(update, 26);

		assertEquals(TIMER_MODEL_AND_VIEW_NAME, timerController.variable(model, update));

		final TimerModel timerModel = (TimerModel) model.getAttribute(TIMER_MODEL_AND_VIEW_NAME);
		assertEquals(format(UPTIME), timerModel.getUpTime());
		assertEquals(format(SUN_UPTIME), timerModel.getSunUpTime());
		assertEquals(format(SHADOW_TIME), timerModel.getShadowTime());
		assertEquals(format(SUN_DOWNTIME), timerModel.getSunDownTime());
		assertEquals(update, timerModel.isUpdate());
	}
	
	
	@Test
	void variableTemperatureLessThanShadowTemperature() {
		prepareVariable(false, 24.9);

		assertEquals(TIMER_MODEL_AND_VIEW_NAME, timerController.variable(model, false));

		final TimerModel timerModel = (TimerModel) model.getAttribute(TIMER_MODEL_AND_VIEW_NAME);
		assertEquals(format(UPTIME), timerModel.getUpTime());
		assertEquals(format(SUN_UPTIME), timerModel.getSunUpTime());
		assertNull(timerModel.getShadowTime());
		assertEquals(format(SUN_DOWNTIME), timerModel.getSunDownTime());
		assertFalse(timerModel.isUpdate());
	}

	private void prepareVariable(final boolean update, final double temperature) {
		parameters.put(Key.SunUpDownType, TwilightType.Civil);
		parameters.put(Key.ShadowTemperature, 25d);
		parameters.put(Key.UpTime, UPTIME);
		parameters.put(Key.ShadowTime, SHADOW_TIME);
		final var date = update ? LocalDate.now() :  LocalDate.now().plusDays(1);
		when(calendarService.cycle(date)).thenReturn(cycle);
		when(configurationService.parameters(RuleKey.EndOfDay, cycle)).thenReturn(parameters);
		when(calendarService.sunUpTime(date, TwilightType.Civil)).thenReturn(Optional.of(SUN_UPTIME));
		when(calendarService.sunDownTime(date, TwilightType.Civil)).thenReturn(Optional.of(SUN_DOWNTIME));
		when(weatherService.maxForecastTemperature(date)).thenReturn(Optional.of(temperature));
	}

	private String format(final LocalTime time) {
		return DateTimeFormatter.ofPattern(TimerController.TIME_PATTERN).format(time);

	}
	@Test
	void timerCancel() {
		assertEquals(VariableController.REDIRECT_VARIABLE_VIEW_NAME, timerController.timerCancel());
	}
	@Test
	void timer() {
		final var timerModel = new TimerModel();
		timerModel.setUpTime(format(UPTIME));
		timerModel.setSunUpTime(format(SUN_UPTIME));
		timerModel.setShadowTime(format(SHADOW_TIME));
		timerModel.setSunDownTime(format(SUN_DOWNTIME));
		
		Mockito.doAnswer(a -> time(a)).when(conversionService).convert(Mockito.anyString(), Mockito.any());
		Mockito.doAnswer(a -> ""+ a.getArgument(0, Double.class)).when(conversionService).convert(Mockito.anyDouble(), Mockito.any());
		
		
		timerController.timer(timerModel, bindingResults);
		
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<List<SystemVariable>> systemVariablesListCapture = ArgumentCaptor.forClass(List.class);
		
		Mockito.verify(systemVariableService).update(systemVariablesListCapture.capture());
		
		final List<SystemVariable> results = systemVariablesListCapture.getValue();
		assertEquals(2,results.size());
		
		assertEquals(TimerController.SYSTEM_VARIABLE_NAME_DAILY_EVENTS,   results.get(0).getName());
		
		//assertEquals(String.format("T0:%s;T1:7.45;T2:9.0;T6:17.45", timerModel.getUpTime()), results.get(0).getValue());
		
		assertEquals(TimerController.SYSTEM_VARIABLE_NAME_TIMER_EVENTS,   results.get(1).getName());
		assertEquals("0", results.get(1).getValue());
		
	}


	private LocalTime time(InvocationOnMock a) {
		final var values = a.getArgument(0, String.class).split("[:]");
return LocalTime.of(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
	}
}
