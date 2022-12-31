package de.mq.iot2.calendar.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class CalendarController {

	static final String DAY_GROUP_NAME = "dayGroup";
	static final String CYCLES_LIST_NAME = "cycles";
	static final String DAY_GROUPS_LIST_NAME = "dayGroups";
	static final String CALENDAR_VIEW_NAME = "calendar";
	static final String REDIRECT_CALENDAR_PATTERN = "redirect:" + CALENDAR_VIEW_NAME + "?dayGroupId=%s";
	private final CalendarService calendarService;
	private final ModelMapper<DayGroup, DayGroupModel> dayGroupMapper;
	private final ModelMapper<Cycle, CycleModel> cycleMapper;
	private final ModelMapper<Day<?>, DayModel> dayMapper;

	CalendarController(CalendarService calendarService, final ModelMapper<DayGroup, DayGroupModel> dayGroupMapper, final ModelMapper<Cycle, CycleModel> cycleMapper,
			final ModelMapper<Day<?>, DayModel> dayMapper) {
		this.calendarService = calendarService;
		this.dayGroupMapper = dayGroupMapper;
		this.cycleMapper = cycleMapper;
		this.dayMapper = dayMapper;
	}

	@GetMapping(value = "/calendar")
	String calendar(final Model model, @RequestParam(name = "dayGroupId", required = false) final String dayGroupId) {
		model.addAllAttributes(initModel(Optional.ofNullable(dayGroupId)));
		return CALENDAR_VIEW_NAME;
	}

	private Map<String, Object> initModel(final Optional<String> dayGroupId) {

		final Map<String, Object> attributes = new HashMap<>();
		final Map<String, DayGroupModel> dayGroupMap = calendarService.dayGroups().stream().map(dayGroup -> dayGroupMapper.toWeb(dayGroup))
				.collect(Collectors.toMap(DayGroupModel::getId, Function.identity()));

		final Collection<DayGroupModel> dayGroups = dayGroupMap.values().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).collect(Collectors.toList());

		final Collection<CycleModel> cycles = calendarService.cycles().stream().map(cycle -> cycleMapper.toWeb(cycle)).sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
				.collect(Collectors.toList());
		attributes.put(DAY_GROUPS_LIST_NAME, dayGroups);
		attributes.put(CYCLES_LIST_NAME, cycles);

		dayGroupId.ifPresentOrElse(id -> attributes.put(DAY_GROUP_NAME, dayGroupMap.containsKey(id) ? mapDaysInto(dayGroupMap.get(id)) : firstDayGroupIfExistsOrNew(dayGroups)),
				() -> attributes.put(DAY_GROUP_NAME, firstDayGroupIfExistsOrNew(dayGroups)));

		return Collections.unmodifiableMap(attributes);
	}

	private DayGroupModel firstDayGroupIfExistsOrNew(final Collection<DayGroupModel> dayGroups) {
		return dayGroups.stream().findFirst().map(x -> mapDaysInto(x)).orElse(new DayGroupModel());
	}

	private DayGroupModel mapDaysInto(final DayGroupModel dayGroup) {
		Assert.notNull(dayGroup, "DayGroup required");
		Assert.hasText(dayGroup.getId(), "Id is required.");
		dayGroup.setDays(dayMapper.toWeb(calendarService.days(dayGroupMapper.toDomain(dayGroup.getId()))).stream().sorted().collect(Collectors.toList()));
		return dayGroup;
	}

	@PostMapping(value = "/searchDayGroup")
	String search(@ModelAttribute(DAY_GROUP_NAME) @Valid final DayGroupModel dayGroupModel, final BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return CALENDAR_VIEW_NAME;
		}

		return String.format(REDIRECT_CALENDAR_PATTERN, dayGroupModel.getId());
	}

}
