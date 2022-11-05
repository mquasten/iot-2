package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.DayGroup;

@Service
class CalendarServiceImp implements CalendarService {

	private final CycleRepository cycleRepository;
	private final DayGroupRepository dayGroupRepository;
	private final DayRepository dayRepository;

	private final Collection<DayOfWeek> weekendDays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
	private final Collection<Entry<Integer, String>> gaussDays = Set.of(new SimpleImmutableEntry<>(-2, "Karfreitag"), new SimpleImmutableEntry<>(0, "Ostersonntag"),
			new SimpleImmutableEntry<>(1, "Ostermontag"), new SimpleImmutableEntry<>(39, "Christi Himmelfahrt"), new SimpleImmutableEntry<>(49, "Pfingstsonntag"),
			new SimpleImmutableEntry<>(50, "Pfingstmontag"), new SimpleImmutableEntry<>(60, "Fronleichnam"));
	private final Collection<Entry<MonthDay, String>> publicHolidays = Set.of(new SimpleImmutableEntry<>(MonthDay.of(1, 1), "Neujahr"), new SimpleImmutableEntry<>(MonthDay.of(5, 1), "Tag der Arbeit"),
			new SimpleImmutableEntry<>(MonthDay.of(10, 3), "Tag der Deutschen Einheit"), new SimpleImmutableEntry<>(MonthDay.of(11, 1), "Allerheiligen"),
			new SimpleImmutableEntry<>(MonthDay.of(12, 25), "1. Weihnachtsfeiertag"), new SimpleImmutableEntry<>(MonthDay.of(12, 26), "2. Weihnachtsfeiertag"));

	@Autowired
	CalendarServiceImp(final CycleRepository cycleRepository, final DayGroupRepository dayGroupRepository, final DayRepository dayRepository) {
		this.cycleRepository = cycleRepository;
		this.dayGroupRepository = dayGroupRepository;
		this.dayRepository = dayRepository;
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
	public Cycle cycle(final LocalDate date) {
		final var defaultCycle = DataAccessUtils.requiredSingleResult(cycleRepository.findByDefaultCycle(true));

		return dayRepository.findAll().stream().filter(day -> day.matches(date)).map(day -> day.dayGroup().cycle())
				.sorted((Comparator<Cycle>) (c1, c2) -> Integer.signum(c1.priority() - c2.priority())).findFirst().orElse(defaultCycle);
	}

}
