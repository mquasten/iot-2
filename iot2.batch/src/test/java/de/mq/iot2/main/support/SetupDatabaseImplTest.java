package de.mq.iot2.main.support;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;

class SetupDatabaseImplTest {

	private final CalendarService calendarService = Mockito.mock(CalendarService.class);
	private final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

	private final SetupDatabase setupDatabase = new SetupDatabase(calendarService, configurationService);

	@Test
	void run() throws Exception {
		setupDatabase.execute();

		Mockito.verify(calendarService).createDefaultCyclesGroupsAndDays();
		Mockito.verify(configurationService).createDefaultConfigurationsAndParameters();
	}

}
