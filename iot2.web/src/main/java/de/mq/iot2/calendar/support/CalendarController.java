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
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class CalendarController {
	
	private  final CalendarService calendarService ;
	private final ModelMapper<DayGroup, DayGroupModel> dayGroupMapper;
	
	CalendarController(CalendarService calendarService, final ModelMapper<DayGroup, DayGroupModel> dayGroupMapper) {
		this.calendarService = calendarService;
		this.dayGroupMapper=dayGroupMapper;
	}

	
	
	@GetMapping(value = "/calendar")
	String configuration(final Model model, @RequestParam(name = "dayGroupId", required = false) final String dayGroupId) {

		model.addAllAttributes(initModel(Optional.ofNullable(dayGroupId)));

		return "calendar";
	}
	
	private Map<String, Object> initModel(final Optional<String> dayGroupId) {
		
		
		final Map<String, Object> attributes = new HashMap<>();
		final Map<String, DayGroupModel> dayGroupMap = calendarService.dayGroups().stream().map(dayGroup -> dayGroupMapper.toWeb(dayGroup)).collect(Collectors.toMap(DayGroupModel::getId, Function.identity()));

		final Collection<DayGroupModel> dayGroups = dayGroupMap.values().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).collect(Collectors.toList());
		attributes.put("dayGroups", dayGroups);

		
		dayGroupId.ifPresentOrElse(
				id -> attributes.put("dayGroup",
						dayGroupMap.containsKey(id) ? mapDaysInto(dayGroupMap.get(id)) : firstDayGroupIfExistsOrNew(dayGroups)),
				() -> attributes.put("dayGroup", firstDayGroupIfExistsOrNew(dayGroups)));
		
		
		return Collections.unmodifiableMap(attributes);
	}
	
	private DayGroupModel firstDayGroupIfExistsOrNew(final Collection<DayGroupModel> dayGroups) {

		return dayGroups.stream().findFirst().map(x -> mapDaysInto(x)).orElse(new DayGroupModel());

	}

	
	private DayGroupModel mapDaysInto(final DayGroupModel dayGroup) {
		Assert.notNull(dayGroup, "DayGroup required");
		Assert.hasText(dayGroup.getId(), "Id is required.");
		//configuration.setParameters(parameterMapper.toWeb(configurationService.parameters(configuration.getId())));
		return dayGroup;
	}
	
	@PostMapping(value = "/searchDayGroup")
	String search(@ModelAttribute("dayGroup") @Valid final DayGroupModel dayGroupModel, final BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "calendar";
		}

		return String.format("redirect:calendar?dayGroupId=%s", dayGroupModel.getId());
	}

}
