package de.mq.iot2.main.support;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.configuration.Configuration.RuleKey;

class CleanUpBatchImplTest {
	
	private final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private final CalendarService calendarService = Mockito.mock(CalendarService.class);
	private final CleanUpBatchImpl cleanUpBatch = new CleanUpBatchImpl(calendarService, configurationService);
	
	@Test
	void cleanUpLocalDateDays() {
		int daysBack = 30;
		Mockito.when(configurationService.parameter(RuleKey.CleanUp, Key.DaysBack, Integer.class)).thenReturn(Optional.of(daysBack));
		
		cleanUpBatch.cleanUpLocalDateDays();
		
		Mockito.verify(calendarService).deleteLocalDateDays(daysBack);
	}
	
	@Test
	void cleanUpLocalDateDaysDaysBackEmpty() {
		Mockito.when(configurationService.parameter(RuleKey.CleanUp, Key.DaysBack, Integer.class)).thenReturn(Optional.empty());
		
		cleanUpBatch.cleanUpLocalDateDays();
		
		Mockito.verify(calendarService, Mockito.never()).deleteLocalDateDays(Mockito.anyInt());
	}

}
