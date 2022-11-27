package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

@Service
class CalendarServiceImp implements CalendarService {

	private final CycleRepository cycleRepository;
	private final DayGroupRepository dayGroupRepository;
	private final DayRepository dayRepository;
	private final double longitude;
	private final double latitude;
	private final Collection<DayOfWeek> weekendDays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
	private final Collection<Entry<Integer, String>> gaussDays = Set.of(new SimpleImmutableEntry<>(-2, "Karfreitag"), new SimpleImmutableEntry<>(0, "Ostersonntag"),
			new SimpleImmutableEntry<>(1, "Ostermontag"), new SimpleImmutableEntry<>(39, "Christi Himmelfahrt"), new SimpleImmutableEntry<>(49, "Pfingstsonntag"),
			new SimpleImmutableEntry<>(50, "Pfingstmontag"), new SimpleImmutableEntry<>(60, "Fronleichnam"));
	private final Collection<Entry<MonthDay, String>> publicHolidays = Set.of(new SimpleImmutableEntry<>(MonthDay.of(1, 1), "Neujahr"), new SimpleImmutableEntry<>(MonthDay.of(5, 1), "Tag der Arbeit"),
			new SimpleImmutableEntry<>(MonthDay.of(10, 3), "Tag der Deutschen Einheit"), new SimpleImmutableEntry<>(MonthDay.of(11, 1), "Allerheiligen"),
			new SimpleImmutableEntry<>(MonthDay.of(12, 25), "1. Weihnachtsfeiertag"), new SimpleImmutableEntry<>(MonthDay.of(12, 26), "2. Weihnachtsfeiertag"));

	@Autowired
	CalendarServiceImp(final CycleRepository cycleRepository, final DayGroupRepository dayGroupRepository, final DayRepository dayRepository, @Value("${iot2.calendar.latitude}") final double latitude,
			@Value("${iot2.calendar.longitude}") final double longitude) {
		this.cycleRepository = cycleRepository;
		this.dayGroupRepository = dayGroupRepository;
		this.dayRepository = dayRepository;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	@Transactional
	public void createDefaultCyclesGroupsAndDays() {

		final var cycle = cycleRepository.save(new CycleImpl(1L, "Freizeit", 101));
		cycleRepository.save(new CycleImpl(2L, "Arbeitstage", 102, true));
		createOrUpdatePublicHolidays(cycle);
		createOrUpdateWeekend(cycle);
		createOrUpdateVacation(cycle);
	}

	private void createOrUpdateVacation(final Cycle cycle) {
		dayGroupRepository.save(new DayGroupImpl(cycle, 3L, "Urlaub", false));
	}

	private void createOrUpdateWeekend(final Cycle cycle) {
		final var weekendGroup = dayGroupRepository.save(new DayGroupImpl(cycle, 2L, "Wochenende"));
		deleteDaysFromDayGroup(weekendGroup);

		final var locale = Locale.GERMAN;
		final var textStyle = TextStyle.FULL;

		weekendDays.stream().map(dayOfWeek -> new DayOfWeekDayImpl(weekendGroup, dayOfWeek, dayOfWeek.getDisplayName(textStyle, locale))).forEach(dayRepository::save);
	}

	private void createOrUpdatePublicHolidays(final Cycle cycle) {
		final var publicHolidayGroup = dayGroupRepository.save(new DayGroupImpl(cycle, 1L, "Feiertage"));

		deleteDaysFromDayGroup(publicHolidayGroup);

		Stream.concat(gaussDays.stream().map(entry -> new GaussDayImpl(publicHolidayGroup, entry.getKey(), entry.getValue())),
				publicHolidays.stream().map(entry -> new DayOfMonthImpl(publicHolidayGroup, entry.getKey(), entry.getValue()))).forEach(dayRepository::save);

	}

	private void deleteDaysFromDayGroup(final DayGroup publicHolidayGroup) {
		dayRepository.findByDayGroup(publicHolidayGroup).forEach(dayRepository::delete);
	}

	@Override
	@Transactional
	public Cycle cycle(final LocalDate date) {
		final var defaultCycle = DataAccessUtils.requiredSingleResult(cycleRepository.findByDefaultCycle(true));

		return dayRepository.findAll().stream().filter(day -> day.matches(date)).map(day -> day.dayGroup().cycle())
				.sorted((Comparator<Cycle>) (c1, c2) -> Integer.signum(c1.priority() - c2.priority())).findFirst().orElse(defaultCycle);
	}

	@Override
	public TimeType timeType(final LocalDate date) {

		final var startSummerTime = lastSundayInMonth(Year.of(date.getYear()), Month.MARCH);

		final var startWinterTime = lastSundayInMonth(Year.of(date.getYear()), Month.OCTOBER);

		if (afterEquals(date, startSummerTime) && date.isBefore(startWinterTime)) {
			return TimeType.Summer;
		}

		return TimeType.Winter;
	}

	private boolean afterEquals(final LocalDate date, final LocalDate startSummerTime) {
		return date.isAfter(startSummerTime) || date.isEqual(startSummerTime);
	}

	private LocalDate lastSundayInMonth(final Year year, final Month month) {
		final var start = LocalDate.of(year.getValue(), Month.of(month.getValue() + 1), 1);
		return IntStream.range(1, 8).mapToObj(i -> start.minusDays(i)).filter(date -> date.getDayOfWeek().equals(DayOfWeek.SUNDAY)).findFirst().get();

	}

	@Override
	public Optional<LocalTime> sunDownTime(final LocalDate date, final TwilightType twilightType) {
		return new SunUpDownCalculatorImpl(latitude, longitude, twilightType).sunDownTime(date.getDayOfYear(), timeType(date).offset());
	}

	@Override
	public Optional<LocalTime> sunUpTime(final LocalDate date, final TwilightType twilightType) {
		return new SunUpDownCalculatorImpl(latitude, longitude, twilightType).sunUpTime(date.getDayOfYear(), timeType(date).offset());
	}

	@Override
	@Transactional
	public int addLocalDateDays(final String name, final LocalDate fromDate, final LocalDate toDate) {
		final Collection<Day<LocalDate>> days = localDateDays(name, fromDate, toDate);
		days.forEach(dayRepository::save);
		return days.size();

	}
	@Override
	@Transactional
	public int deleteLocalDateDays(final String name, final LocalDate fromDate, final LocalDate toDate) {
		final Collection<Day<LocalDate>> days = localDateDays(name, fromDate, toDate);
		days.forEach(dayRepository::delete);
		return days.size();

	}

	private Collection<Day<LocalDate>> localDateDays(final String name, final LocalDate fromDate, final LocalDate toDate) {

		Assert.hasText(name, "Name of dayGroup required.");
		Assert.notNull(fromDate, "FromDate required.");
		Assert.notNull(fromDate, "ToDate required.");
		final var dayGroup = dayGroupRepository.findByName(name).orElseThrow(() -> new IncorrectResultSizeDataAccessException(String.format("DayGroup %s not found", name), 1, 0));

		if (dayGroup.readOnly()) {
			throw new IllegalStateException("DayGroup is readonly.");
		}

		final Long numberOfDays = fromDate.until(toDate, ChronoUnit.DAYS);
		return  IntStream.rangeClosed(0, numberOfDays.intValue()).mapToObj(i -> new LocalDateDayImp(dayGroup, fromDate.plusDays(i))).collect(Collectors.toList());
	}

}
