package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class DayController {
	private final CalendarService calendarService;
	private final ModelMapper<Day<?>, DayModel> dayMapper;
	DayController(final CalendarService calendarService, final ModelMapper<Day<?>, DayModel> dayMapper) {
		this.dayMapper = dayMapper;
		this.calendarService = calendarService;
	}
	
	@PostMapping(value = "/deleteDay")
	String deleteDay(@ModelAttribute("day") @Valid final DayModel dayModel) {
		final Day<?> day = dayMapper.toDomain(dayModel.getId());
		calendarService.deleteDay(day);
		return  String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, IdUtil.getId(day.dayGroup()));
		
	}
	@PostMapping(value = "/editDays", params = "localDate")
	String editLocaldates(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, Model model) {
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setDayGroupId(dayGroupModel.getId());
		localDateModel.setDayGroupName(dayGroupModel.getName());
		model.addAttribute("localDate", localDateModel);
		return "localDate";
		
	}

	@PostMapping(value = "/editLocalDate", params = "operation")
	String editLocalDate(@ModelAttribute("localDate") @Valid final LocalDateModel localDateModel, final BindingResult bindingResult, final Locale locale, @RequestParam(name = "operation") final String operation) {
		
		if( bindingResult.hasErrors() ) {
			return "localDate";
		}
		
		final DateTimeFormatter  formatter =   DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
	    final LocalDate from=  LocalDate.parse(localDateModel.getFrom(), formatter);
		final LocalDate to =  LocalDate.parse(StringUtils.hasText(localDateModel.getTo()) ? localDateModel.getTo() : localDateModel.getFrom(),formatter);
		final Map<String, Supplier<Integer>> operations = Map.of("add", () -> calendarService.addLocalDateDays(localDateModel.getDayGroupName(), from, to), "delete", () -> calendarService.deleteLocalDateDays(localDateModel.getDayGroupName(), from, to));
		Assert.isTrue(operations.containsKey(operation), String.format("Unsupported Operation %s", operation) );
		operations.get(operation).get();
		return String.format(CalendarController.REDIRECT_CALENDAR_PATTERN, localDateModel.getDayGroupId());
		
	}
}
