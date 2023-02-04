package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;
import de.mq.iot2.weather.WeatherService;
import jakarta.validation.Valid;

@Controller
class TimerController {
	
	private static final int MINUTES_IN_FUTURE = 5;

	static final String TIMER_MODEL_AND_VIEW_NAME= "timer";
	
	static final String REDIRECT_TIMER_VIEW_NAME= "redirect:"+ TIMER_MODEL_AND_VIEW_NAME+ "?update=%s";
	
	private final ConfigurationService configurationService;
	private final CalendarService calendarService;
	private final WeatherService weatherService;
	private final ConversionService conversionService ;
	private final SystemVariableService systemVariableService;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	TimerController(final SystemVariableService systemVariableService, final ConfigurationService configurationService, final CalendarService calendarService, final WeatherService weatherService, final ConversionService conversionService) {
		this.systemVariableService=systemVariableService;
		this.configurationService=configurationService;
		this.calendarService=calendarService;
		this.weatherService=weatherService;
		this.conversionService=conversionService;
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
		if(bindingResult.hasErrors()) {
			return TIMER_MODEL_AND_VIEW_NAME;
		}
		
		final List<Entry<String, Double>> timers = timers(timerModel);
		
		final double  limit = timerModel.isUpdate()?  timeAsDouble(LocalTime.now().plusMinutes(MINUTES_IN_FUTURE)): 0;
		
		
		if(minTimer(timers) < limit) {
			
			bindingResult.addError(new ObjectError(TIMER_MODEL_AND_VIEW_NAME, new String[] { "error.time.future" }, new Integer[] { MINUTES_IN_FUTURE},
					"{error.time.future}"));
			return TIMER_MODEL_AND_VIEW_NAME;
		}
		
		systemVariableService.update(List.of(systemVariable(timers, timerModel.isUpdate()), new SystemVariable("TimerEvents", "0")));
		
		
		return VariableController.REDIRECT_VARIABLE_VIEW_NAME;
	}

	private Double minTimer(final List<Entry<String, Double>> timers) {
		if(timers.size()==0) {
			return Double.MAX_VALUE;
		}
		return timers.get(0).getValue();
	}

	private SystemVariable  systemVariable(final List<Entry<String, Double>> timers, final boolean update) {
		return new SystemVariable(update?"EventExecutions":"DailyEvents", StringUtils.collectionToDelimitedString(timers.stream().map(entry -> String.format("%s:%s", entry.getKey(), conversionService.convert(entry.getValue(), String.class))).collect(Collectors.toList()), ";" ))	; 
	
	}

	private List<Entry<String, Double>> timers(final TimerModel timerModel) {
		final List<Entry<String, Double>> timers = new ArrayList<>();
		timer("T0", timerModel.getUpTime()).ifPresent(timers::add);
		timer("T1" , timerModel.getSunUpTime()).ifPresent(timers::add);
		timer("T2", timerModel.getShadowTime()).ifPresent(timers::add);
		timer("T6", timerModel.getSunDownTime()).ifPresent(timers::add);
		
		Collections.sort(timers, (e1,e2) -> e1.getValue().compareTo(e2.getValue()));
		return timers;
	}
	
	
	private Optional<Entry<String, Double>> timer(final String name, final String value) {
		if(!StringUtils.hasText(value)) {
			return Optional.empty();
		}
		final var time = conversionService.convert(value, LocalTime.class);
		
		final double timeAsDouble  = timeAsDouble(time);
		return Optional.of(new SimpleImmutableEntry<>(name, timeAsDouble));
	}

	private double timeAsDouble(final LocalTime time) {
		return time.getHour() + 1e-2*time.getMinute();
	}

}
