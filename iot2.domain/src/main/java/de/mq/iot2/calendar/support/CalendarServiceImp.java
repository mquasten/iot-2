package de.mq.iot2.calendar.support;

import java.time.MonthDay;

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
