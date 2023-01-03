package de.mq.iot2.calendar.support;

import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class DayController {
	static final String MESSAGE_KEY_DAY_EXISTS = "error.day.exists";
	static final String DAY_OF_WEEK_LIST = "days";
	static final String LOCAL_DATE_MODEL_AND_VIEW_NAME = "localDate";
	static final String DAY_MODEL_AND_VIEW_NAME = "day";
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
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, IdUtil.getId(day.dayGroup()));

	}

	@PostMapping(value = "/editDays", params = LOCAL_DATE_MODEL_AND_VIEW_NAME)
	String editLocaldates(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, final Model model) {
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setDayGroupId(dayGroupModel.getId());
		localDateModel.setDayGroupName(dayGroupModel.getName());
		model.addAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME, localDateModel);
		return LOCAL_DATE_MODEL_AND_VIEW_NAME;

	}

	@PostMapping(value = "/editDays", params = "dayOfWeek")
	String addDayOfWeek(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, Model model, final Locale locale) {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(dayGroupModel.getId());
		dayModel.setType(DayOfWeekDayImpl.class.getName());
		model.addAttribute(DAY_MODEL_AND_VIEW_NAME, dayModel);

		model.addAttribute(DAY_OF_WEEK_LIST, daysOfWeek(locale));
		return DAY_MODEL_AND_VIEW_NAME;

	}

	@PostMapping(value = "/editDays", params = "dayOfMonth")
	String addDayMonth(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, Model model) {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(dayGroupModel.getId());
		dayModel.setType(DayOfMonthImpl.class.getName());
		model.addAttribute(DAY_MODEL_AND_VIEW_NAME, dayModel);
		return DAY_MODEL_AND_VIEW_NAME;

	}

	private Collection<Entry<String, String>> daysOfWeek(final Locale locale) {
		return calendarService.unUsedDaysOfWeek().stream()
				.map(day -> new SimpleImmutableEntry<>(String.valueOf(day.getValue()), day.getDisplayName(TextStyle.SHORT_STANDALONE, locale))).collect(Collectors.toList());
	}

	@PostMapping(value = "/editLocalDate", params = "add")
	String addLocalDate(@ModelAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME) @Valid final LocalDateModel localDateModel, final BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return LOCAL_DATE_MODEL_AND_VIEW_NAME;
		}
		final Long expectedNumberOfDays = 1 + localDateModel.getFromDate().until(localDateModel.getToDate(), ChronoUnit.DAYS);

		final int numberOfDays = calendarService.addLocalDateDays(localDateModel.getDayGroupName(), localDateModel.getFromDate(), localDateModel.getToDate());

		if (expectedNumberOfDays.intValue() != numberOfDays) {
			bindingResult.addError(new ObjectError(DAY_MODEL_AND_VIEW_NAME, new String[] { "error.date.exists" }, new Integer[] { numberOfDays, expectedNumberOfDays.intValue() },
					"{error.date.exists}"));
			return LOCAL_DATE_MODEL_AND_VIEW_NAME;
		}

		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
	}

	@PostMapping(value = "/editLocalDate", params = "delete")
	String deleteLocalDate(@ModelAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME) @Valid final LocalDateModel localDateModel, final BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return LOCAL_DATE_MODEL_AND_VIEW_NAME;
		}
		calendarService.deleteLocalDateDays(localDateModel.getDayGroupName(), localDateModel.getFromDate(), localDateModel.getToDate());
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
	}

	@PostMapping(value = "/editLocalDate", params = "cancel")
	String cancelLocalDate(@ModelAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME) final LocalDateModel localDateModel) {
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
	}

	@PostMapping(value = "/addDay", params = "cancel")
	String cancelAdd(@ModelAttribute(DAY_MODEL_AND_VIEW_NAME) final DayModel dayModel) {
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, dayModel.getDayGroupId());
	}

	@PostMapping(value = "/addDay", params = "add")
	String addDay(@ModelAttribute(DAY_MODEL_AND_VIEW_NAME) @Valid() final DayModel dayModel, final BindingResult bindingResult, final Model model, final Locale locale) {
		if (bindingResult.hasErrors()) {
			addDaysIfDayOfWeek(dayModel, model, locale);
			return DAY_MODEL_AND_VIEW_NAME;
		}

		if (!calendarService.createDayIfNotExists(dayMapper.toDomain(dayModel))) {
			addDaysIfDayOfWeek(dayModel, model, locale);
			bindingResult.addError(new ObjectError(DAY_MODEL_AND_VIEW_NAME, new String[] { MESSAGE_KEY_DAY_EXISTS }, null, String.format("{%s}", MESSAGE_KEY_DAY_EXISTS)));
			return DAY_MODEL_AND_VIEW_NAME;
		}
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, dayModel.getDayGroupId());
	}

	private void addDaysIfDayOfWeek(final DayModel dayModel, Model model, final Locale locale) {
		if (!dayModel.targetEntity().equals(DayOfWeekDayImpl.class)) {
			return;
		}

		model.addAttribute(DAY_OF_WEEK_LIST, daysOfWeek(locale));
	}

}
