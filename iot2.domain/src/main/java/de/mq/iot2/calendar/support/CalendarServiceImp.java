package de.mq.iot2.calendar.support;

import java.time.MonthDay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;

@Service
class CalendarServiceImp implements CalendarService {

	private final DayGroupRepository dayGroupRepository;

	@Autowired
	CalendarServiceImp(final DayGroupRepository dayGroupRepository) {
		this.dayGroupRepository = dayGroupRepository;
	}

	@Override
	public final void createDefaultGroupsAndDays() {
		final var publicHolidayGroup = new DayGroupImpl(1L, "Feiertage");

		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(1, 1), "Neujahr"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(5, 1), "Tag der Arbeit"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(10, 3), "Tag der Deutschen Einheit"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(11, 1), "Allerheiligen"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(12, 25), "1. Weihnachtsfeiertag"));
		publicHolidayGroup.assign(new DayOfMonthImpl(publicHolidayGroup, MonthDay.of(12, 26), "2. Weihnachtsfeiertag"));

		dayGroupRepository.save(publicHolidayGroup);
	}

}
