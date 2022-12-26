package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class DayController {
	private static final String LOCAL_DATE_MODEL_AND_VIEW_NAME = "localDate";
	
	private static final String DAY_OF_WEEK_MODEL_AND_VIEW_NAME = "dayOfWeek";
	
	private final CalendarService calendarService;
	private final ModelMapper<Day<?>, DayModel> dayMapper;
	
	DayController(final CalendarService calendarService, final ModelMapper<Day<?>, DayModel> dayMapper) {
		this.dayMapper = dayMapper;
		this.calendarService = calendarService;
	}
	
	@PostMapping(value = "/deleteDay")
	String deleteDay(@ModelAttribute("day") final DayModel dayModel) {
		final Day<?> day = dayMapper.toDomain(dayModel.getId());
		calendarService.deleteDay(day);
		return  String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, IdUtil.getId(day.dayGroup()));
		
	}
	@PostMapping(value = "/editDays", params = LOCAL_DATE_MODEL_AND_VIEW_NAME)
	String editLocaldates(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, Model model) {
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setDayGroupId(dayGroupModel.getId());
		localDateModel.setDayGroupName(dayGroupModel.getName());
		model.addAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME, localDateModel);
		return LOCAL_DATE_MODEL_AND_VIEW_NAME;
		
	}
	
	@PostMapping(value = "/editDays", params = DAY_OF_WEEK_MODEL_AND_VIEW_NAME)
	String addDayOfWeek(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, Model model, final Locale locale) {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(dayGroupModel.getId());
		model.addAttribute(DAY_OF_WEEK_MODEL_AND_VIEW_NAME, dayModel);
		
		model.addAttribute("days", daysOfWeek(locale));
		return DAY_OF_WEEK_MODEL_AND_VIEW_NAME;
		
	}
	
	@PostMapping(value = "/editDays", params = "dayMonth")
	String addDayMonth(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, Model model) {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(dayGroupModel.getId());
		model.addAttribute("dayMonth", dayModel);
		return "dayMonth";
		
	}

	private Collection<Entry<String, String>> daysOfWeek(final Locale locale) {
		return  calendarService.unUsedDaysOfWeek().stream().map(day -> new SimpleImmutableEntry<>(String.valueOf(day.getValue()), day.getDisplayName(TextStyle.SHORT_STANDALONE, locale))).collect(Collectors.toList());
	}

	@PostMapping(value = "/editLocalDate", params = "add")
	String addLocalDate(@ModelAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME) @Valid final LocalDateModel localDateModel, final BindingResult bindingResult) {
		
		if( bindingResult.hasErrors() ) {
			return LOCAL_DATE_MODEL_AND_VIEW_NAME;
		}
		calendarService.addLocalDateDays(localDateModel.getDayGroupName(), localDateModel.getFromDate(), localDateModel.getToDate());
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
	}
	
	@PostMapping(value = "/editLocalDate", params = "delete")
	String deleteLocalDate(@ModelAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME) @Valid final LocalDateModel localDateModel, final BindingResult bindingResult) {
		if( bindingResult.hasErrors() ) {
			return LOCAL_DATE_MODEL_AND_VIEW_NAME;
		}
		calendarService.deleteLocalDateDays(localDateModel.getDayGroupName(), localDateModel.getFromDate(), localDateModel.getToDate());
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
	}
	
	@PostMapping(value = "/editLocalDate", params = "cancel")
	String cancelLocalDate(@ModelAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME) final LocalDateModel localDateModel) {
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
	}
	
	
	@PostMapping(value = "/addDayOfWeek", params = "cancel")
	String cancelDayOfWeek(@ModelAttribute(DAY_OF_WEEK_MODEL_AND_VIEW_NAME) final DayModel dayOfWeekModel) {
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, dayOfWeekModel.getDayGroupId());
	}
	
	@PostMapping(value = "/addDayOfWeek", params = "add")
	String addDayOfWeek(@ModelAttribute(DAY_OF_WEEK_MODEL_AND_VIEW_NAME) @Valid() final DayModel dayModel,final BindingResult bindingResult, final Model model, final Locale locale) {
		if( bindingResult.hasErrors() ) {
			model.addAttribute("days", daysOfWeek(locale));
			return DAY_OF_WEEK_MODEL_AND_VIEW_NAME;
		}

		dayModel.setValueType(DayOfWeek.class);
		calendarService.createDayIfNotExists(dayMapper.toDomain(dayModel));
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, dayModel.getDayGroupId());
	}
	
	@PostMapping(value = "/addDayMonth", params = "add")
	String addDayMonth(@ModelAttribute("dayMonth") @Valid() final DayModel dayModel,final BindingResult bindingResult, final Model model) {
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, dayModel.getDayGroupId());
	}
	
	@PostMapping(value = "/addDayMonth", params = "cancel")
	String cancelDayMonth(@ModelAttribute("dayMonth") final DayModel dayModel) {
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, dayModel.getDayGroupId());
	}
}
