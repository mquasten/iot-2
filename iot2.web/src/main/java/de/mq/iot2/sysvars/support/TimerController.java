package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

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
import jakarta.validation.Valid;

@Controller
class TimerController {
	
	static final String TIMER_MODEL_AND_VIEW_NAME= "timer";
	
	static final String REDIRECT_TIMER_VIEW_NAME= "redirect:"+ TIMER_MODEL_AND_VIEW_NAME+ "?update=%s";
	
	private final ConfigurationService configurationService;
	private final CalendarService calendarService;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	TimerController(final ConfigurationService configurationService, final CalendarService calendarService) {
		this.configurationService=configurationService;
		this.calendarService=calendarService;
	}
	
	@GetMapping(value = "/timer")
	String variable(final Model model, @RequestParam(name = "update", required = false, defaultValue = "false") final boolean update, final Locale locale) {
	
		final LocalDate date = update? LocalDate.now() : LocalDate.now().plusDays(1);
		
		final var twilightType = configurationService.parameter(RuleKey.EndOfDay, Key.SunUpDownType, TwilightType.class).orElse(TwilightType.Mathematical);
		
		final Cycle cycle =calendarService.cycle(date);
		
		final Map<Key,Object> parameters = configurationService.parameters(RuleKey.EndOfDay, cycle);
		
		final TimerModel timerModel = new TimerModel();
		timerModel.setUpdate(update);	
		if( parameters.containsKey(Key.UpTime)) {
			timerModel.setUpTime(format((LocalTime) parameters.get(Key.UpTime)));
		}
		
		calendarService.sunUpTime(date, twilightType).ifPresent(time -> timerModel.setSunUpTime(format(time)));
		
		calendarService.sunDownTime(date, twilightType).ifPresent(time -> timerModel.setSunDownTime(format(time)));
		System.out.println(parameters);
		
	
		model.addAttribute(TIMER_MODEL_AND_VIEW_NAME, timerModel);
		return TIMER_MODEL_AND_VIEW_NAME;
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
