package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.weather.WeatherService;
import jakarta.validation.Valid;

@Controller
class TimerController {
	
	static final String TIMER_MODEL_AND_VIEW_NAME= "timer";
	
	static final String REDIRECT_TIMER_VIEW_NAME= "redirect:"+ TIMER_MODEL_AND_VIEW_NAME+ "?update=%s";
	
	private final ConfigurationService configurationService;
	private final CalendarService calendarService;
	private final WeatherService weatherService;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	TimerController(final ConfigurationService configurationService, final CalendarService calendarService, final WeatherService weatherService) {
		this.configurationService=configurationService;
		this.calendarService=calendarService;
		this.weatherService=weatherService;
	}
	
	@GetMapping(value = "/timer")
	String variable(final Model model, @RequestParam(name = "update", required = false, defaultValue = "false") final boolean update, final Locale locale) {
		final var date = update? LocalDate.now() : LocalDate.now().plusDays(1);
		final Cycle cycle =calendarService.cycle(date);
		final Map<Key,Object> parameters = configurationService.parameters(RuleKey.EndOfDay, cycle);
		final var twilightType = value(parameters, Key.SunUpDownType, TwilightType.class).orElse(TwilightType.Mathematical);
		final var timerModel = new TimerModel();
		timerModel.setUpdate(update);	
		final var temperatureLimit = value(parameters, Key.ShadowTemperature, Double.class).orElse(Double.MAX_VALUE);
		value(parameters, Key.UpTime,LocalTime.class).ifPresent(time -> timerModel.setUpTime( format(time)));
		value(parameters, Key.ShadowTime, LocalTime.class).ifPresent(time ->  weatherService.maxForecastTemperature(date).filter( temperatur -> temperatur >= temperatureLimit ).ifPresent(temperature -> timerModel.setShadowTime(format(time))) );
		calendarService.sunUpTime(date, twilightType).ifPresent(time -> timerModel.setSunUpTime(format(time)));
		calendarService.sunDownTime(date, twilightType).ifPresent(time -> timerModel.setSunDownTime(format(time)));
		model.addAttribute(TIMER_MODEL_AND_VIEW_NAME, timerModel);
		return TIMER_MODEL_AND_VIEW_NAME;
	}
	

	@SuppressWarnings("unchecked")
	private <T> Optional<T>value(final Map<Key,Object> parameters, final Key key, final Class<T> clazz) {
		 return (Optional<T>) Optional.ofNullable( parameters.get(key));
	}
	
	private String format(final LocalTime time) {
		return time.format(dateTimeFormatter);
	}
	
	@PostMapping(value = "/timer", params="cancel" )
	String timerCancel() {
		return VariableController.REDIRECT_VARIABLE_VIEW_NAME;
	}
	
	@PostMapping(value = "/timer", params="save" )
	String timer(@ModelAttribute(TIMER_MODEL_AND_VIEW_NAME) @Valid final TimerModel timerModel, final BindingResult bindingResult) {
		
		System.out.println(timerModel.isUpdate());
		return TIMER_MODEL_AND_VIEW_NAME;
	}

}
