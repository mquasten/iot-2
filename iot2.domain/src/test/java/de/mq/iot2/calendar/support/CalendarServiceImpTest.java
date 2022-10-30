package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

class CalendarServiceImpTest {
	private static final String VALUE_FIELD_NAME = "value";
	private final CycleRepository cycleRepository = Mockito.mock(CycleRepository.class);
	private final DayGroupRepository dayGroupRepository = Mockito.mock(DayGroupRepository.class);
	private final DayRepository dayRepository = Mockito.mock(DayRepository.class);

	private final CalendarService calendarService = new CalendarServiceImp(cycleRepository, dayGroupRepository, dayRepository);

	@Test
	void createDefaultCyclesGroupsAndDays() {

		final Map<String, Cycle> savedCyles = new HashMap<>();
		final Map<String, DayGroup> savedDayGroups = new HashMap<>();
		final Map<Class<?>, Collection<Day<?>>> savedDays = new HashMap<>();

		Mockito.doAnswer(answer -> {
			final var cycle = answer.getArgument(0, Cycle.class);
			savedCyles.put(cycle.name(), cycle);
			return cycle;
		}).when(cycleRepository).save(Mockito.any(Cycle.class));

		Mockito.doAnswer(answer -> {
			final DayGroup dayGroup = answer.getArgument(0, DayGroup.class);
			savedDayGroups.put(dayGroup.name(), dayGroup);
			return dayGroup;
		}).when(dayGroupRepository).save(Mockito.any(DayGroup.class));

		Mockito.doAnswer(answer -> {
			final Day<?> day = answer.getArgument(0, Day.class);
			if (!savedDays.containsKey(day.getClass())) {
				savedDays.put(day.getClass(), new ArrayList<>());
			}
			savedDays.get(day.getClass()).add(day);
			return day;
		}).when(dayRepository).save(Mockito.any(Day.class));

		calendarService.createDefaultCyclesGroupsAndDays();

		assertEquals(2, savedDays.get(DayOfWeekDayImpl.class).size());
		assertEquals(7, savedDays.get(GaussDayImpl.class).size());
		assertEquals(6, savedDays.get(DayOfMonthImpl.class).size());

		final DayGroup weekendDayDayGroup = savedDays.get(DayOfWeekDayImpl.class).stream().map(Day::dayGroup).findAny().get();
		final Collection<DayOfWeek> expectedDaysOfWeek = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		assertEquals(savedDayGroups.get(weekendDayDayGroup.name()), weekendDayDayGroup);
		savedDays.get(DayOfWeekDayImpl.class).stream().forEach(day -> {
			assertEquals(weekendDayDayGroup, day.dayGroup());
			assertTrue(expectedDaysOfWeek.contains(day.value()));
		});

		final DayGroup gaussDayGroup = savedDays.get(GaussDayImpl.class).stream().map(Day::dayGroup).findAny().get();
		final Collection<Integer> expectedGaussDays = Set.of(-2, 0, 1, 39, 49, 50, 60);
		assertEquals(savedDayGroups.get(gaussDayGroup.name()), gaussDayGroup);
		savedDays.get(GaussDayImpl.class).stream().forEach(day -> {
			assertEquals(gaussDayGroup, day.dayGroup());
			assertTrue(expectedGaussDays.contains(ReflectionTestUtils.getField(day, VALUE_FIELD_NAME)));
		});

		final DayGroup fixedDayGroup = savedDays.get(DayOfMonthImpl.class).stream().map(Day::dayGroup).findAny().get();
		final Collection<MonthDay> expectedFixedDays = Set.of(MonthDay.of(1, 1), MonthDay.of(5, 1), MonthDay.of(10, 3), MonthDay.of(11, 1), MonthDay.of(12, 25), MonthDay.of(12, 26));
		assertEquals(savedDayGroups.get(fixedDayGroup.name()), fixedDayGroup);
		savedDays.get(DayOfMonthImpl.class).stream().forEach(day -> {
			assertEquals(fixedDayGroup, day.dayGroup());
			assertTrue(expectedFixedDays.contains(day.value()));
		});

		final Cycle nonWorkingDayCycle = savedCyles.get(weekendDayDayGroup.cycle().name());
		assertEquals(nonWorkingDayCycle, weekendDayDayGroup.cycle());
		assertEquals(nonWorkingDayCycle, gaussDayGroup.cycle());
		assertEquals(nonWorkingDayCycle, fixedDayGroup.cycle());
		assertFalse(nonWorkingDayCycle.isDeaultCycle());

		savedCyles.values().stream().filter(c -> !c.equals(nonWorkingDayCycle)).forEach(c -> assertTrue(c.isDeaultCycle()));
	}

}
