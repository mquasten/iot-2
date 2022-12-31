package de.mq.iot2.calendar.support;

import static de.mq.iot2.calendar.support.CalendarController.CALENDAR_VIEW_NAME;
import static de.mq.iot2.calendar.support.CalendarController.CYCLES_LIST_NAME;
import static de.mq.iot2.calendar.support.CalendarController.DAY_GROUPS_LIST_NAME;
import static de.mq.iot2.calendar.support.CalendarController.DAY_GROUP_NAME;
import static de.mq.iot2.calendar.support.CalendarController.REDIRECT_CALENDAR_PATTERN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

class CalendarControllerTest {

	private final CalendarService calendarService = mock(CalendarService.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<DayGroup, DayGroupModel> dayGroupMapper = mock(ModelMapper.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<Cycle, CycleModel> cycleMapper = mock(ModelMapper.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<Day<?>, DayModel> dayMapper = mock(ModelMapper.class);

	private final CalendarController calendarController = new CalendarController(calendarService, dayGroupMapper, cycleMapper, dayMapper);

	private final Model model = new ExtendedModelMap();
	private final DayGroupModel dayGroupModel01 = mock(DayGroupModel.class);
	private final DayGroupModel dayGroupModel02 = mock(DayGroupModel.class);
	private final CycleModel cycleModel01 = mock(CycleModel.class);
	private final CycleModel cycleModel02 = mock(CycleModel.class);
	private final Collection<DayModel> dayModelList01 = List.of(mock(DayModel.class));
	private final Collection<DayModel> dayModelList02 = List.of(mock(DayModel.class));
	private final BindingResult bindingResults = mock(BindingResult.class);
	private String groupId;

	@BeforeEach
	void setup() {
		final Cycle cycle01 = new CycleImpl(1L, "Cycle01", 0);
		final Cycle cycle02 = new CycleImpl(2L, "Cycle02", 0);
		final DayGroup dayGroup01 = new DayGroupImpl(cycle01, 1L, "dayGroup01");

		when(dayGroupModel01.getId()).thenReturn(IdUtil.getId(dayGroup01));
		when(dayGroupModel01.getName()).thenReturn(dayGroup01.name());

		final DayGroup dayGroup02 = new DayGroupImpl(cycle02, 2L, "dayGroup02");
		groupId = IdUtil.getId(dayGroup02);
		when(dayGroupModel02.getId()).thenReturn(IdUtil.getId(dayGroup02));
		when(dayGroupModel02.getName()).thenReturn(dayGroup02.name());
		when(calendarService.dayGroups()).thenReturn(List.of(dayGroup02, dayGroup01));
		when(dayGroupMapper.toWeb(dayGroup01)).thenReturn(dayGroupModel01);
		when(dayGroupMapper.toWeb(dayGroup02)).thenReturn(dayGroupModel02);

		when(calendarService.cycles()).thenReturn(List.of(cycle02, cycle01));

		when(cycleModel01.getId()).thenReturn(IdUtil.getId(cycle01));
		when(cycleModel01.getName()).thenReturn(cycle01.name());
		when(cycleMapper.toWeb(cycle01)).thenReturn(cycleModel01);

		when(cycleModel02.getId()).thenReturn(IdUtil.getId(cycle02));
		when(cycleModel02.getName()).thenReturn(cycle02.name());
		when(cycleMapper.toWeb(cycle02)).thenReturn(cycleModel02);

		when(dayGroupMapper.toDomain(IdUtil.getId(dayGroup01))).thenReturn(dayGroup01);
		when(dayGroupMapper.toDomain(IdUtil.getId(dayGroup02))).thenReturn(dayGroup02);

		final Collection<Day<?>> dayList01 = List.of(mock(Day.class));
		when(calendarService.days(dayGroup01)).thenReturn(dayList01);
		final Collection<Day<?>> dayList02 = List.of(mock(Day.class));
		when(calendarService.days(dayGroup02)).thenReturn(dayList02);

		when(dayMapper.toWeb(dayList01)).thenReturn(dayModelList01);
		when(dayMapper.toWeb(dayList02)).thenReturn(dayModelList02);
	}

	@Test
	void calendar() {
		assertEquals(CALENDAR_VIEW_NAME, calendarController.calendar(model, null));

		final List<?> dayGroups = (List<?>) model.getAttribute(DAY_GROUPS_LIST_NAME);
		assertEquals(2, dayGroups.size());
		assertEquals(dayGroupModel01, dayGroups.get(0));
		assertEquals(dayGroupModel02, dayGroups.get(1));

		final List<?> cycles = (List<?>) model.getAttribute(CYCLES_LIST_NAME);
		assertEquals(2, cycles.size());
		assertEquals(cycleModel01, cycles.get(0));
		assertEquals(cycleModel02, cycles.get(1));

		assertEquals(dayGroupModel01, model.getAttribute(DAY_GROUP_NAME));
		verify(dayGroupModel01).setDays(dayModelList01);
	}

	@Test
	void calendarWithId() {
		assertEquals(CALENDAR_VIEW_NAME, calendarController.calendar(model, groupId));

		final List<?> dayGroups = (List<?>) model.getAttribute(DAY_GROUPS_LIST_NAME);
		assertEquals(2, dayGroups.size());
		assertEquals(dayGroupModel01, dayGroups.get(0));
		assertEquals(dayGroupModel02, dayGroups.get(1));

		final List<?> cycles = (List<?>) model.getAttribute(CYCLES_LIST_NAME);
		assertEquals(2, cycles.size());
		assertEquals(cycleModel01, cycles.get(0));
		assertEquals(cycleModel02, cycles.get(1));

		assertEquals(dayGroupModel02, model.getAttribute(DAY_GROUP_NAME));
		verify(dayGroupModel02).setDays(dayModelList02);
	}

	@Test
	void calendarWithMissingId() {
		assertEquals(CALENDAR_VIEW_NAME, calendarController.calendar(model, UUID.randomUUID().toString()));

		final List<?> dayGroups = (List<?>) model.getAttribute(DAY_GROUPS_LIST_NAME);
		assertEquals(2, dayGroups.size());
		assertEquals(dayGroupModel01, dayGroups.get(0));
		assertEquals(dayGroupModel02, dayGroups.get(1));

		final List<?> cycles = (List<?>) model.getAttribute(CYCLES_LIST_NAME);
		assertEquals(2, cycles.size());
		assertEquals(cycleModel01, cycles.get(0));
		assertEquals(cycleModel02, cycles.get(1));

		assertEquals(dayGroupModel01, model.getAttribute(DAY_GROUP_NAME));
		verify(dayGroupModel01).setDays(dayModelList01);
	}

	@Test
	void search() {
		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, dayGroupModel01.getId()), calendarController.search(dayGroupModel01, bindingResults));
	}

	@Test
	void searchValidationErrors() {
		when(bindingResults.hasErrors()).thenReturn(true);
		
		assertEquals(CALENDAR_VIEW_NAME , calendarController.search(dayGroupModel01, bindingResults));
	}
}
