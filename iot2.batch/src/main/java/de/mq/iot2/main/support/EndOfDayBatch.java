package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;


@Service
public class EndOfDayBatch {
	
	private final CalendarService calendarService;

	EndOfDayBatch(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}
	

	public final void execute(final Optional<LocalDate> date) {
		LocalDate runDate = date.orElse(LocalDate.now().plusDays(1) );
		System.out.println("Use date:" + runDate);
		
		final var cycle = calendarService.cycle(runDate);
		
		System.out.println(cycle);

	}


}
