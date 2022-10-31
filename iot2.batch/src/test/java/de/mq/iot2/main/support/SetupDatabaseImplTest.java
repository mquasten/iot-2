package de.mq.iot2.main.support;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;

import de.mq.iot2.calendar.CalendarService;

class SetupDatabaseImplTest {

	private final CalendarService calendarService = Mockito.mock(CalendarService.class);

	private final CommandLineRunner commandLineRunner = new SetupDatabaseImpl(calendarService);

	@Test
	void run() throws Exception {
		commandLineRunner.run();

		Mockito.verify(calendarService).createDefaultCyclesGroupsAndDays();
	}

}
