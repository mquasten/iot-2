package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.DayGroup;

@Service
class CalendarServiceImp implements CalendarService {

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
	CalendarServiceImp(final DayGroupRepository dayGroupRepository, final DayRepository dayRepository) {
		this.dayGroupRepository = dayGroupRepository;
		this.dayRepository = dayRepository;
	}

	@Override
	@Transactional
	public void createDefaultGroupsAndDays() {
		createOrUpdatePublicHolidays();
		createOrUpdateWeekend();
		createOrUpdateVacation();
	}

	private void createOrUpdateVacation() {
		dayGroupRepository.save(new DayGroupImpl(3L, "Urlaub", false));
	}

	private void createOrUpdateWeekend() {
		final var weekendGroup = dayGroupRepository.save(new DayGroupImpl(2L, "Wochenende"));
		deleteDaysFromDayGroup(weekendGroup);

		final var locale = Locale.GERMAN;
		final var textStyle = TextStyle.FULL;

		weekendDays.stream().map(dayOfWeek -> new DayOfWeekDayImpl(weekendGroup, dayOfWeek, dayOfWeek.getDisplayName(textStyle, locale))).forEach(dayRepository::save);
	}

	private void createOrUpdatePublicHolidays() {
		final var publicHolidayGroup = dayGroupRepository.save(new DayGroupImpl(1L, "Feiertage"));

		deleteDaysFromDayGroup(publicHolidayGroup);

		Stream.concat(gaussDays.stream().map(entry -> new GaussDayImpl(publicHolidayGroup, entry.getKey(), entry.getValue())),
				publicHolidays.stream().map(entry -> new DayOfMonthImpl(publicHolidayGroup, entry.getKey(), entry.getValue()))).forEach(dayRepository::save);

	}

	private void deleteDaysFromDayGroup(final DayGroup publicHolidayGroup) {
		dayRepository.findByDayGroup(publicHolidayGroup).forEach(dayRepository::delete);
	}

}
