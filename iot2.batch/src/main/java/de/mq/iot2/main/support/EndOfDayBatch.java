package de.mq.iot2.main.support;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;

@Service
public class EndOfDayBatch  {

	private final CalendarService calendarService;

	EndOfDayBatch(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	@BatchMethod(value="end-of-day", converterClass = OneOrNoneLocalDateArgumentConverter.class)
	final void execute(final LocalDate date) {
		System.out.println("Use date:" + date);

		final var cycle = calendarService.cycle(date);

		System.out.println(cycle);

	}
	
	final void execute() {
		execute(LocalDate.now());
	}

	
}
