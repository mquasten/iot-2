package de.mq.iot2.calendar.support;

import static de.mq.iot2.calendar.support.CalendarServiceImp.DAYS_BACK_INVALID_MESSAGE;
import static de.mq.iot2.calendar.support.CalendarServiceImp.DAY_GROUP_NOT_FOUND_MESSAGE;
import static de.mq.iot2.calendar.support.CalendarServiceImp.DAY_GROUP_READONLY_MESSAGE;
import static de.mq.iot2.calendar.support.CalendarServiceImp.LIMIT_OF_DAYS_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;

class CalendarServiceImpTest {
	private static final String CSV_DELIMITER = ";";
	private static final double LONGITUDE = 6.2815922;
	private static final double LATITUDE = 51.1423399;
	private static final String VALUE_FIELD_NAME = "value";
	private final CycleRepository cycleRepository = Mockito.mock(CycleRepository.class);
	private final DayGroupRepository dayGroupRepository = Mockito.mock(DayGroupRepository.class);
	private final DayRepository dayRepository = Mockito.mock(DayRepository.class);
	@SuppressWarnings("unchecked")
	private final Converter<Pair<Day<?>, boolean[]>, String[]> dayCsvConverter = Mockito.mock(Converter.class);
	@SuppressWarnings("unchecked")
	private final Converter<Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>>, Day<?>> arrayCsvConverter = Mockito.mock(Converter.class);
	private final static int DAY_LIMIT = 30;

	private final CalendarService calendarService = new CalendarServiceImp(cycleRepository, dayGroupRepository, dayRepository, dayCsvConverter, arrayCsvConverter, LATITUDE,
			LONGITUDE, DAY_LIMIT, ZoneUtil.ZONE_ID_EUROPEAN_SUMMERTIME.getId(), CSV_DELIMITER);

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
		final Collection<MonthDay> expectedFixedDays = Set.of(MonthDay.of(1, 1), MonthDay.of(5, 1), MonthDay.of(10, 3), MonthDay.of(11, 1), MonthDay.of(12, 25),
				MonthDay.of(12, 26));
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

		final DayGroup otherTimesDayGroup = savedDayGroups.get(CalendarServiceImp.OTHER_UP_TIMES_GROUP_NAME);
		assertFalse(otherTimesDayGroup.cycle().isDeaultCycle());

		savedCyles.values().stream().filter(c -> !c.equals(nonWorkingDayCycle) && !c.equals(otherTimesDayGroup.cycle())).forEach(c -> assertTrue(c.isDeaultCycle()));
	}

	@Test
	void cycle() {
		final var date = LocalDate.now();
		final var winner = new CycleImpl(RandomTestUtil.randomString(), 1);
		final var days = List.of(day(date, new CycleImpl(RandomTestUtil.randomString(), 0), false), day(date, new CycleImpl(RandomTestUtil.randomString(), 2), true),
				day(date, new CycleImpl(RandomTestUtil.randomString(), 3), true), day(date, winner, true), day(date, new CycleImpl(RandomTestUtil.randomString(), 0), false));
		Mockito.when(cycleRepository.findByDefaultCycle(true)).thenReturn(Collections.singleton(new CycleImpl(RandomTestUtil.randomString(), 4711)));
		Mockito.when(dayRepository.findAll()).thenReturn(days);

		assertEquals(winner, calendarService.cycle(date));
	}

	private Day<?> day(LocalDate date, final Cycle cycle, boolean matching) {
		final var day = Mockito.mock(Day.class);
		Mockito.when(day.matches(date)).thenReturn(matching);
		DayGroup dayGroup = Mockito.mock(DayGroup.class);
		Mockito.when(day.dayGroup()).thenReturn(dayGroup);
		Mockito.when(dayGroup.cycle()).thenReturn(cycle);
		return day;
	}

	@Test
	void cycleDefaultCycle() {
		final var date = LocalDate.now();
		final var days = List.of(day(date, new CycleImpl(RandomTestUtil.randomString(), 0), false), day(date, new CycleImpl(RandomTestUtil.randomString(), 2), true),
				day(date, new CycleImpl(RandomTestUtil.randomString(), 3), true), day(date, new CycleImpl(RandomTestUtil.randomString(), 1), true),
				day(date, new CycleImpl(RandomTestUtil.randomString(), 0), false));
		final var defaultCycle = new CycleImpl(RandomTestUtil.randomString(), 4711);
		Mockito.when(cycleRepository.findByDefaultCycle(true)).thenReturn(Collections.singleton(defaultCycle));
		Mockito.when(dayRepository.findAll()).thenReturn(days);

		assertEquals(defaultCycle, calendarService.cycle(LocalDate.now().plusDays(1)));
	}

	@Test
	final void sunDownTime() {
		assertEquals(Optional.of(LocalTime.of(19, 57)), calendarService.sunDownTime(LocalDate.of(2022, 3, 27), TwilightType.Mathematical));

	}

	@Test
	void sunUpDownTimeNotContinuousSummerWinterTimeChange() {
		assertEquals(61L, Duration.between(calendarService.sunDownTime(LocalDate.of(2022, 3, 26), TwilightType.Mathematical).get(),
				calendarService.sunDownTime(LocalDate.of(2022, 3, 27), TwilightType.Mathematical).get()).toMinutes());
		assertEquals(57L, Duration.between(calendarService.sunUpTime(LocalDate.of(2022, 3, 26), TwilightType.Mathematical).get(),
				calendarService.sunUpTime(LocalDate.of(2022, 3, 27), TwilightType.Mathematical).get()).toMinutes());
		assertEquals(62L, Duration.between(calendarService.sunDownTime(LocalDate.of(2022, 10, 30), TwilightType.Mathematical).get(),
				calendarService.sunDownTime(LocalDate.of(2022, 10, 29), TwilightType.Mathematical).get()).toMinutes());
		assertEquals(58L, Duration.between(calendarService.sunUpTime(LocalDate.of(2022, 10, 30), TwilightType.Mathematical).get(),
				calendarService.sunUpTime(LocalDate.of(2022, 10, 29), TwilightType.Mathematical).get()).toMinutes());
	}

	@Test
	final void sunUpTime() {
		assertEquals(Optional.of(LocalTime.of(7, 23)), calendarService.sunUpTime(LocalDate.of(2022, 3, 27), TwilightType.Mathematical));
	}

	@Test
	final void addLocalDateDays() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));

		assertEquals(2, calendarService.addLocalDateDays(groupName, LocalDate.now(), LocalDate.now().plusDays(1)));

		Mockito.verify(dayRepository).save(new LocalDateDayImp(dayGroup, LocalDate.now()));
		Mockito.verify(dayRepository).save(new LocalDateDayImp(dayGroup, LocalDate.now().plusDays(1)));
	}

	@Test
	final void addLocalDateDaysAlreadyExists() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));
		final Day<LocalDate> existingDay1 = new LocalDateDayImp(Mockito.mock(DayGroup.class), LocalDate.now());
		final Day<LocalDate> existingDay2 = new LocalDateDayImp(Mockito.mock(DayGroup.class), LocalDate.now().plusDays(1));
		Mockito.when(dayRepository.findById(IdUtil.getId(existingDay1))).thenReturn(Optional.of(existingDay1));
		Mockito.when(dayRepository.findById(IdUtil.getId(existingDay2))).thenReturn(Optional.of(existingDay2));

		assertEquals(0, calendarService.addLocalDateDays(groupName, LocalDate.now(), LocalDate.now().plusDays(1)));

		Mockito.verify(dayRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	final void addLocalDateDaysDayGroupNotFound() {
		final var dayGroup = RandomTestUtil.randomString();
		assertEquals(String.format(DAY_GROUP_NOT_FOUND_MESSAGE, dayGroup),
				assertThrows(IncorrectResultSizeDataAccessException.class, () -> calendarService.addLocalDateDays(dayGroup, LocalDate.now(), LocalDate.now())).getMessage());

	}

	@Test
	final void addLocalDateDaysDayGroupReadonly() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));
		assertEquals(DAY_GROUP_READONLY_MESSAGE,
				assertThrows(IllegalStateException.class, () -> calendarService.addLocalDateDays(groupName, LocalDate.now(), LocalDate.now())).getMessage());

	}

	@Test
	final void addLocalDateDaysMandatoryParameters() {
		assertThrows(IllegalArgumentException.class, () -> calendarService.addLocalDateDays(null, LocalDate.now(), LocalDate.now()));
		assertThrows(IllegalArgumentException.class, () -> calendarService.addLocalDateDays(RandomTestUtil.randomString(), null, LocalDate.now()));
		assertThrows(IllegalArgumentException.class, () -> calendarService.addLocalDateDays(RandomTestUtil.randomString(), LocalDate.now(), null));
	}

	@Test
	final void addLocalDateDaysFromBeforeTo() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));

		assertEquals(0, calendarService.addLocalDateDays(groupName, LocalDate.now().plusDays(1), LocalDate.now()));
	}

	@Test
	final void addLocalDateDaysDayLimit() {
		assertEquals(String.format(LIMIT_OF_DAYS_MESSAGE, DAY_LIMIT), assertThrows(IllegalArgumentException.class,
				() -> calendarService.addLocalDateDays(RandomTestUtil.randomString(), LocalDate.now(), LocalDate.now().plusDays(DAY_LIMIT + 1))).getMessage());
	}

	@Test
	final void deleteLocalDateDays() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));
		final var day1 = new LocalDateDayImp(dayGroup, LocalDate.now());
		final var day2 = new LocalDateDayImp(dayGroup, LocalDate.now().plusDays(1));
		Mockito.when(dayRepository.findById(IdUtil.getId(day1))).thenReturn(Optional.of(day1));
		Mockito.when(dayRepository.findById(IdUtil.getId(day2))).thenReturn(Optional.of(day2));

		assertEquals(2, calendarService.deleteLocalDateDays(groupName, LocalDate.now(), LocalDate.now().plusDays(1)));

		Mockito.verify(dayRepository).delete(day1);
		Mockito.verify(dayRepository).delete(day2);
	}

	@Test
	final void deleteLocalDateDaysNotExists() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));
		final var day1 = new LocalDateDayImp(dayGroup, LocalDate.now());
		final var day2 = new LocalDateDayImp(dayGroup, LocalDate.now().plusDays(1));

		assertEquals(0, calendarService.deleteLocalDateDays(groupName, LocalDate.now(), LocalDate.now().plusDays(1)));

		Mockito.verify(dayRepository, Mockito.never()).delete(day1);
		Mockito.verify(dayRepository, Mockito.never()).delete(day2);
	}

	@Test
	final void deleteLocalDateDaysOtherGroup() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));
		final var day1 = new LocalDateDayImp(Mockito.mock(DayGroup.class), LocalDate.now());
		final var day2 = new LocalDateDayImp(Mockito.mock(DayGroup.class), LocalDate.now().plusDays(1));
		Mockito.when(dayRepository.findById(IdUtil.getId(day1))).thenReturn(Optional.of(day1));
		Mockito.when(dayRepository.findById(IdUtil.getId(day2))).thenReturn(Optional.of(day2));

		assertEquals(0, calendarService.deleteLocalDateDays(groupName, LocalDate.now(), LocalDate.now().plusDays(1)));

		Mockito.verify(dayRepository, Mockito.never()).delete(day1);
		Mockito.verify(dayRepository, Mockito.never()).delete(day2);
	}

	@Test
	final void deleteLocalDateDaysDayGroupNotFound() {
		final var dayGroup = RandomTestUtil.randomString();
		assertEquals(String.format(DAY_GROUP_NOT_FOUND_MESSAGE, dayGroup),
				assertThrows(IncorrectResultSizeDataAccessException.class, () -> calendarService.deleteLocalDateDays(dayGroup, LocalDate.now(), LocalDate.now())).getMessage());

	}

	@Test
	final void deleteLocalDateDaysDayGroupReadonly() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName);
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));
		assertEquals(DAY_GROUP_READONLY_MESSAGE,
				assertThrows(IllegalStateException.class, () -> calendarService.deleteLocalDateDays(groupName, LocalDate.now(), LocalDate.now())).getMessage());

	}

	@Test
	final void deleteLocalDateDaysMandatoryParameters() {
		assertThrows(IllegalArgumentException.class, () -> calendarService.deleteLocalDateDays(null, LocalDate.now(), LocalDate.now()));
		assertThrows(IllegalArgumentException.class, () -> calendarService.deleteLocalDateDays(RandomTestUtil.randomString(), null, LocalDate.now()));
		assertThrows(IllegalArgumentException.class, () -> calendarService.deleteLocalDateDays(RandomTestUtil.randomString(), LocalDate.now(), null));
	}

	@Test
	final void deleteLocalDateDaysFromBeforeTo() {
		final var groupName = RandomTestUtil.randomString();
		final var dayGroup = new DayGroupImpl(Mockito.mock(Cycle.class), groupName, false);
		final var day1 = new LocalDateDayImp(dayGroup, LocalDate.now());
		final var day2 = new LocalDateDayImp(dayGroup, LocalDate.now().plusDays(1));
		Mockito.when(dayRepository.findById(IdUtil.getId(day1))).thenReturn(Optional.of(day1));
		Mockito.when(dayRepository.findById(IdUtil.getId(day2))).thenReturn(Optional.of(day2));
		Mockito.when(dayGroupRepository.findByName(groupName)).thenReturn(Optional.of(dayGroup));

		assertEquals(0, calendarService.deleteLocalDateDays(groupName, LocalDate.now().plusDays(1), LocalDate.now()));
	}

	@Test
	final void deleteLocalDateDaysDayLimit() {
		assertEquals(String.format(LIMIT_OF_DAYS_MESSAGE, DAY_LIMIT), assertThrows(IllegalArgumentException.class,
				() -> calendarService.deleteLocalDateDays(RandomTestUtil.randomString(), LocalDate.now(), LocalDate.now().plusDays(DAY_LIMIT + 1))).getMessage());
	}

	@Test
	final void deleteLocalDateDaysCleanup() {
		@SuppressWarnings("unchecked")
		final Day<LocalDate> dayEqulasLimit = Mockito.mock(Day.class);
		@SuppressWarnings("unchecked")
		final Day<LocalDate> dayBeforeLimit = Mockito.mock(Day.class);
		@SuppressWarnings("unchecked")
		final Day<LocalDate> dayAfterLimit = Mockito.mock(Day.class);
		Mockito.when(dayEqulasLimit.value()).thenReturn(LocalDate.now().minusDays(DAY_LIMIT));
		Mockito.when(dayBeforeLimit.value()).thenReturn(LocalDate.now().minusDays(2 * DAY_LIMIT));
		Mockito.when(dayAfterLimit.value()).thenReturn(LocalDate.now().minusDays(DAY_LIMIT - 1));
		Mockito.when(dayRepository.findAllLocalDateDays()).thenReturn(List.of(dayEqulasLimit, dayBeforeLimit, dayAfterLimit));

		assertEquals(2, calendarService.deleteLocalDateDays(DAY_LIMIT));

		Mockito.verify(dayRepository).delete(dayEqulasLimit);
		Mockito.verify(dayRepository).delete(dayBeforeLimit);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1 })
	final void deleteLocalDateDaysCleanupInvalidDaysBack(final int daysBack) {
		assertEquals(DAYS_BACK_INVALID_MESSAGE, assertThrows(IllegalArgumentException.class, () -> calendarService.deleteLocalDateDays(daysBack)).getMessage());
	}

	@Test
	void dayGroups() {
		final Collection<DayGroup> dayGroups = List.of(Mockito.mock(DayGroup.class), Mockito.mock(DayGroup.class));

		Mockito.when(dayGroupRepository.findAll()).thenReturn(dayGroups);

		assertEquals(dayGroups, calendarService.dayGroups());
	}

	@Test
	void cycles() {
		final Collection<Cycle> cycles = List.of(Mockito.mock(Cycle.class), Mockito.mock(Cycle.class));

		Mockito.when(cycleRepository.findAll()).thenReturn(cycles);

		assertEquals(cycles, calendarService.cycles());
	}

	@Test
	void days() {
		final DayGroup dayGroup = Mockito.mock(DayGroup.class);
		final Collection<Day<?>> days = List.of(Mockito.mock(Day.class), Mockito.mock(Day.class));

		Mockito.when(dayRepository.findByDayGroup(dayGroup)).thenReturn(days);

		assertEquals(days, calendarService.days(dayGroup));
	}

	@Test
	void daysNull() {
		assertThrows(IllegalArgumentException.class, () -> calendarService.days(null));
	}

	@Test
	void deleteDay() {
		final Day<?> day = new LocalDateDayImp(Mockito.mock(DayGroup.class), LocalDate.now());

		calendarService.deleteDay(day);

		Mockito.verify(dayRepository).delete(day);
	}

	@Test
	void deleteDayDayGroupReadonly() {
		final DayGroup dayGroup = Mockito.mock(DayGroup.class);
		Mockito.when(dayGroup.readOnly()).thenReturn(true);
		final Day<?> day = new LocalDateDayImp(dayGroup, LocalDate.now());

		assertEquals(CalendarServiceImp.DAY_GROUP_READONLY_MESSAGE, assertThrows(IllegalArgumentException.class, () -> calendarService.deleteDay(day)).getMessage());
	}

	@Test
	void deleteDayInvalid() {
		assertThrows(IllegalArgumentException.class, () -> calendarService.deleteDay(Mockito.mock(Day.class)));
		assertThrows(IllegalArgumentException.class, () -> calendarService.deleteDay(null));
	}

	@Test
	void unUsedDaysOfWeek() {
		final DayGroup dayGroup = Mockito.mock(DayGroup.class);
		final Collection<Day<DayOfWeek>> existing = List.of(DayOfWeek.values()).stream().filter(dayOfWeek -> dayOfWeek != DayOfWeek.FRIDAY)
				.map(dayOfWeek -> new DayOfWeekDayImpl(dayGroup, dayOfWeek)).collect(Collectors.toList());
		Mockito.when(dayRepository.findAllDayOfWeekDays()).thenReturn(existing);

		final Collection<DayOfWeek> results = calendarService.unUsedDaysOfWeek();

		assertEquals(1, results.size());
		assertEquals(DayOfWeek.FRIDAY, results.stream().findAny().get());
	}

	@Test
	void createDayIfNotExists() {
		final Day<MonthDay> day = new DayOfMonthImpl(Mockito.mock(DayGroup.class), MonthDay.of(12, 25));
		Mockito.when(dayRepository.findById(IdUtil.getId(day))).thenReturn(Optional.empty());

		assertTrue(calendarService.createDayIfNotExists(day));

		Mockito.verify(dayRepository).save(day);
	}

	@Test
	void createDayIfNotExistsAlreadyExists() {
		final Day<MonthDay> day = new DayOfMonthImpl(Mockito.mock(DayGroup.class), MonthDay.of(12, 25));
		Mockito.when(dayRepository.findById(IdUtil.getId(day))).thenReturn(Optional.of(day));

		assertFalse(calendarService.createDayIfNotExists(day));

		Mockito.verify(dayRepository, Mockito.never()).save(day);
	}

	@Test
	void createDayIfNotExistsInvalid() {
		final Day<MonthDay> day = new DayOfMonthImpl(Mockito.mock(DayGroup.class), MonthDay.of(12, 25));
		ReflectionTestUtils.setField(day, "id", null);

		final var required = "required.";
		assertTrue(assertThrows(TypeMismatchDataAccessException.class, () -> calendarService.createDayIfNotExists(day)).getMessage().contains("value is null"));
		assertTrue(assertThrows(IllegalArgumentException.class, () -> calendarService.createDayIfNotExists(null)).getMessage().contains(required));

		final Day<?> day2 = new DayOfMonthImpl(Mockito.mock(DayGroup.class), MonthDay.of(12, 25));
		ReflectionTestUtils.setField(day2, "dayGroup", null);
		assertTrue(assertThrows(IllegalArgumentException.class, () -> calendarService.createDayIfNotExists(day2)).getMessage().contains(required));

	}

	@Test
	void createDayIfNotExistsDayGroupReadOnly() {
		final DayGroup dayGroup = Mockito.mock(DayGroup.class);
		Mockito.when(dayGroup.readOnly()).thenReturn(true);
		final Day<MonthDay> day = new DayOfMonthImpl(dayGroup, MonthDay.of(12, 25));
		assertEquals(CalendarServiceImp.DAY_GROUP_READONLY_MESSAGE, assertThrows(IllegalArgumentException.class, () -> calendarService.createDayIfNotExists(day)).getMessage());
	}

	@Test
	void export() throws IOException {
		final Cycle cycleFreizeit = new CycleImpl("Freizeit", 101);
		final DayGroup dayGroupPublicHoliday = new DayGroupImpl(cycleFreizeit, 1L, "Feiertage");
		final DayGroup dayGroupWeekend = new DayGroupImpl(cycleFreizeit, 2L, "Wochenende");
		final DayGroup dayGroupVacation = new DayGroupImpl(cycleFreizeit, 3L, "Urlaub");
		final CycleImpl cycleSonderzeiten = new CycleImpl("abweichender Tagesbeginn", 100);
		final DayGroup dayGroupOtherTimes = new DayGroupImpl(cycleSonderzeiten, 4L, "Sonderzeiten");
		final Day<?> easterDay = new GaussDayImpl(dayGroupPublicHoliday, 0, "Ostersonntag");
		final Day<?> laborDay = new DayOfMonthImpl(dayGroupPublicHoliday, MonthDay.of(Month.MAY, 1), "Tag der Arbeit");
		final Day<?> sunDay = new DayOfWeekDayImpl(dayGroupWeekend, DayOfWeek.SUNDAY, DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN));
		Mockito.when(dayRepository.findAll()).thenReturn(List.of(sunDay, laborDay, easterDay));
		Mockito.when(dayGroupRepository.findAll()).thenReturn(List.of(dayGroupPublicHoliday, dayGroupWeekend, dayGroupVacation, dayGroupOtherTimes));
		Mockito.doAnswer(a -> {
			@SuppressWarnings("unchecked")
			final Pair<Day<?>, boolean[]> pair = a.getArgument(0, Pair.class);
			return new String[] { IdUtil.getId(pair.getFirst()), !pair.getSecond()[0] ? pair.getFirst().dayGroup().name() : "",
					!pair.getSecond()[1] ? pair.getFirst().dayGroup().cycle().name() : "", pair.getFirst().description().orElse(""), ".öä.." };
		}).when(dayCsvConverter).convert(Mockito.any());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {

			calendarService.export(os);

			final Map<String, Pair<String, String>> results = CollectionUtils.arrayToList(os.toString().split("\n")).stream().map(Object::toString)
					.collect(Collectors.toMap(x -> x.split(String.format("[%s]", CSV_DELIMITER))[0],
							x -> Pair.of(x.split(String.format("[%s]", CSV_DELIMITER))[1], x.split(String.format("[%s]", CSV_DELIMITER))[2])));
			assertEquals(5, results.size());
			assertEquals(dayGroupPublicHoliday.name(), results.get(IdUtil.getId(easterDay)).getFirst());
			assertEquals(cycleFreizeit.name(), results.get(IdUtil.getId(easterDay)).getSecond());
			assertTrue(results.get(IdUtil.getId(laborDay)).getFirst().isEmpty());
			assertTrue(results.get(IdUtil.getId(laborDay)).getSecond().isEmpty());
			assertEquals(results.get(IdUtil.getId(sunDay)).getFirst(), dayGroupWeekend.name());
			assertTrue(results.get(IdUtil.getId(sunDay)).getSecond().isEmpty());
			assertEquals(results.get(id(0)).getFirst(), dayGroupOtherTimes.name());
			assertEquals(results.get(id(0)).getSecond(), cycleSonderzeiten.name());
			assertEquals(results.get(id(1)).getFirst(), dayGroupVacation.name());
			assertTrue(results.get(id(1)).getSecond().isEmpty());

		}
	}

	private String id(int dayOffset) {
		return IdUtil.getId(new LocalDateDayImp(Mockito.mock(DayGroup.class), LocalDate.of(1900, 1, 1).plusDays(dayOffset)));
	}

	@Test
	void parseCsv() {
		final String pattern = "[|](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

		final String[] values = "x| \"y|z\"|a".split(pattern);
		assertEquals(3, values.length);
		for (int i = 0; i < values.length; i++) {
			final String value = StringUtils.trimTrailingCharacter(StringUtils.trimLeadingCharacter(values[i].strip(), '"'), '"');
			assertFalse(value.contains("\""));
		}

	}

	@Test
	void importCsv() throws IOException {
		final Cycle freizeit = new CycleImpl(1L, "Freizeit", 102);
		final DayGroup feiertage = new DayGroupImpl(freizeit, 1L, "Feiertage");
		final DayGroup wochenende = new DayGroupImpl(freizeit, 2L, "Wochenende");

		final Day<?> ostern = new GaussDayImpl(feiertage, 0);
		final Day<?> tagDerArbeit = new DayOfMonthImpl(feiertage, MonthDay.of(5, 1));
		final Day<?> samstag = new DayOfWeekDayImpl(wochenende, DayOfWeek.SATURDAY);
		final Day<?> sonntag = new DayOfWeekDayImpl(wochenende, DayOfWeek.SUNDAY);

		final Cycle arbeitstage = new CycleImpl(2L, "Arbeitstage", 100, true);
		final DayGroup arbeitszeit = new DayGroupImpl(arbeitstage, 5L, "Arbeitszeit");
		final Day<?> dummyDayArbeitszeit = new LocalDateDayImp(arbeitszeit, LocalDate.of(1, 1, 1));

		final Cycle abweichenderTagesbeginn = new CycleImpl(3L, "abweichender Tagesbeginn", 101, false);
		final DayGroup sonderzeiten = new DayGroupImpl(abweichenderTagesbeginn, 4L, "Sonderzeiten");
		final Day<?> dummyDaySonderzeiten = new LocalDateDayImp(sonderzeiten, LocalDate.of(2, 1, 1));

		final DayGroup urlaub = new DayGroupImpl(freizeit, 3L, "Urlaub");
		final Day<?> dummyDayUrlaub = new LocalDateDayImp(urlaub, LocalDate.of(3, 1, 1));

		final Map<String, Day<?>> days = Map.of("0", ostern, "01.05", tagDerArbeit, "6", samstag, "7", sonntag, "01.01.1900", dummyDayArbeitszeit, "02.01.1900",
				dummyDaySonderzeiten, "03.01.1900", dummyDayUrlaub);

		Mockito.doAnswer(answer -> {
			@SuppressWarnings("unchecked")
			final Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>> pair = answer.getArgument(0, Pair.class);
			return days.get(pair.getFirst()[1]);
		}).when(arrayCsvConverter).convert(Mockito.any());

		try (final InputStream is = getClass().getClassLoader().getResourceAsStream("calendar.csv")) {
			calendarService.importCsv(is);
		}

		Mockito.verify(cycleRepository, Mockito.times(1)).save(freizeit);
		Mockito.verify(cycleRepository, Mockito.times(1)).save(arbeitstage);
		Mockito.verify(cycleRepository, Mockito.times(1)).save(abweichenderTagesbeginn);
		Mockito.verifyNoMoreInteractions(cycleRepository);

		Mockito.verify(dayGroupRepository, Mockito.times(1)).save(feiertage);
		Mockito.verify(dayGroupRepository, Mockito.times(1)).save(arbeitszeit);
		Mockito.verify(dayGroupRepository, Mockito.times(1)).save(sonderzeiten);
		Mockito.verify(dayGroupRepository, Mockito.times(1)).save(urlaub);
		Mockito.verify(dayGroupRepository, Mockito.times(1)).save(wochenende);
		Mockito.verifyNoMoreInteractions(dayGroupRepository);

		days.values().forEach(day -> Mockito.verify(dayRepository, Mockito.times(1)).save(day));
		Mockito.verifyNoMoreInteractions(dayRepository);

	}

	@Test
	void importCsvWrongNUmberOfColumns() throws IOException {
		try (final InputStream is = new ByteArrayInputStream(";".getBytes());) {
			assertEquals(String.format(CalendarServiceImp.WRONG_NUMBER_OF_COLUMNS_MESSAGE, 1),
					assertThrows(IllegalArgumentException.class, () -> calendarService.importCsv(is)).getMessage());
		}
	}

	@Test
	void removeCalendar() {
		boolean[] daysDeleted = { false };
		boolean[] dayGroupsDeleted = { false };
		Mockito.doAnswer(_ -> {
			daysDeleted[0] = true;
			return null;
		}).when(dayRepository).deleteAll();
		Mockito.doAnswer(_ -> {
			dayGroupsDeleted[0] = true;
			assertTrue(daysDeleted[0]);
			return null;
		}).when(dayGroupRepository).deleteAll();
		Mockito.doAnswer(_ -> {

			assertTrue(daysDeleted[0] && dayGroupsDeleted[0]);
			return null;
		}).when(cycleRepository).deleteAll();

		calendarService.removeCalendar();

		assertTrue(daysDeleted[0]);
		assertTrue(dayGroupsDeleted[0]);
		Mockito.verify(dayRepository, Mockito.times(1)).deleteAll();
		Mockito.verify(dayGroupRepository, Mockito.times(1)).deleteAll();
		Mockito.verify(cycleRepository, Mockito.times(1)).deleteAll();
	}
}
