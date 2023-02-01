package de.mq.iot2.sysvars.support;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.weather.WeatherService;



@Controller
class VariableController {
	
	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	
	private final WeatherService weatherService;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private final ConversionService conversionService;
	
	static final String VARIABLE_MODEL_AND_VIEW_NAME = "variable";
	
	static final String REDIRECT_VARIABLE_VIEW_NAME = "redirect:" +VARIABLE_MODEL_AND_VIEW_NAME;
	
	
	
	VariableController(final CalendarService calendarService, final ConfigurationService configurationService, final WeatherService weatherService, ConversionService conversionService) {
		this.calendarService = calendarService;
		this.configurationService=configurationService;
		this.weatherService=weatherService;
		this.conversionService=conversionService;
	}

	@GetMapping(value = "/variable")
	String variable(final Model model, @RequestParam(name = "readSystemVariables", required = false) final boolean readSystemVariables, final Locale locale) {
		final VariableModel variableModel = new VariableModel();
		variableModel.setLocale(locale);
		final var twilightType = configurationService.parameter(RuleKey.EndOfDay, Key.SunUpDownType, TwilightType.class).orElse(TwilightType.Mathematical);
		variableModel.setTwilightType(twilightType.name().toLowerCase());
		calendarService.sunUpTime(variableModel.getDate(), twilightType).ifPresent(time -> variableModel.setSunUpToday(format(time)));
		calendarService.sunDownTime(variableModel.getDate(), twilightType).ifPresent(time -> variableModel.setSunDownToday(format(time)));
		calendarService.sunUpTime(variableModel.getDate().plusDays(1), twilightType).ifPresent(time -> variableModel.setSunUpTomorrow(format(time)));
		calendarService.sunDownTime(variableModel.getDate().plusDays(1), twilightType).ifPresent(time -> variableModel.setSunDownTomorrow(format(time)));
		weatherService.maxForecastTemperature(variableModel.getDate()).ifPresent(temperature -> variableModel.setMaxTemperatureToday(conversionService.convert(temperature, String.class)));
		weatherService.maxForecastTemperature(variableModel.getDate().plusDays(1)).ifPresent(temperature -> variableModel.setMaxTemperatureTomorrow(conversionService.convert(temperature, String.class)));
		model.addAttribute(VARIABLE_MODEL_AND_VIEW_NAME, variableModel);
		return VARIABLE_MODEL_AND_VIEW_NAME;
	}
	
	private String format(final LocalTime time) {
		return time.format(dateTimeFormatter);
	}
	
	@PostMapping(value = "/variable", params="today" )
	String updateTimerToday() {
		return String.format(TimerController.REDIRECT_TIMER_VIEW_NAME , true);
	}
	
	@PostMapping(value = "/variable", params="tomorrow" )
	String updateTimerTomorrow() {
		return String.format(TimerController.REDIRECT_TIMER_VIEW_NAME, false);
	}

}
