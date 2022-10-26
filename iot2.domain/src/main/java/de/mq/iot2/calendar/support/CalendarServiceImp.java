package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mq.iot2.calendar.CalendarService;

@Service
class CalendarServiceImp implements CalendarService {

	private final DayGroupRepository dayGroupRepository;

	@Autowired
	CalendarServiceImp(final DayGroupRepository dayGroupRepository) {
		this.dayGroupRepository = dayGroupRepository;
	}

	@Override
	@Transactional
	public void createDefaultGroupsAndDays() {
		createOrUpdatePublicHolidays();
		createOrUpdateWeekend();
	}

	private void createOrUpdateWeekend() {
		final var weekendGroup = new DayGroupImpl(2L, "Wochenende");
		final var locale = Locale.GERMAN;
		final var textStyle = TextStyle.FULL;
		weekendGroup.assign(new DayOfWeekDayImpl(weekendGroup, DayOfWeek.SATURDAY, DayOfWeek.SATURDAY.getDisplayName(textStyle, locale)));
		weekendGroup.assign(new DayOfWeekDayImpl(weekendGroup, DayOfWeek.SUNDAY, DayOfWeek.SUNDAY.getDisplayName(textStyle, locale)));
		dayGroupRepository.save(weekendGroup);
	}

	private void createOrUpdatePublicHolidays() {
		final var publicHolidayGroup = new DayGroupImpl(1L, "Feiertage");

		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(1, 1), "Neujahr"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(5, 1), "Tag der Arbeit"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(10, 3), "Tag der Deutschen Einheit"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(11, 1), "Allerheiligen"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(12, 25), "1. Weihnachtsfeiertag"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(12, 26), "2. Weihnachtsfeiertag"));

		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, -2, "Karfreitag"));
		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, 0, "Ostersonntag"));
		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, 1, "Ostermontag"));
		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, 39, "Christi Himmelfahrt"));
		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, 49, "Pfingstsonntag"));
		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, 50, "Pfingstmontag"));
		publicHolidayGroup.assign(new GaussDayImpl(publicHolidayGroup, 60, "Fronleichnam"));

		dayGroupRepository.save(publicHolidayGroup);
	}

}
