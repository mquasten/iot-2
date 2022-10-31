package de.mq.iot2.calendar;

import java.time.LocalDate;

public interface CalendarService {

	void createDefaultCyclesGroupsAndDays();
	
	Cycle cycle(final LocalDate date);

}