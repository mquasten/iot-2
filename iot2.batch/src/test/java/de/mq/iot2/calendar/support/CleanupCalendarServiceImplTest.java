package de.mq.iot2.calendar.support;

import static de.mq.iot2.configuration.Configuration.RuleKey.CleanUp;
import static de.mq.iot2.configuration.Parameter.Key.DaysBack;

import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Configuration;
import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CleanupCalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Configuration.RuleKey;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

class CleanupCalendarServiceImplTest {
	private static final String CLEANUP_CALENDAR_BATCH_NAME = "cleanup-calendar";
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	private final CalendarService calendarService = mock(CalendarService.class);
	private final ProtocolService protocolService = mock(ProtocolService.class);
	private final CleanupCalendarService cleanupCalendarService = new CleanupCalendarServiceImpl(calendarService, configurationService, protocolService);
	private final Protocol protocol = mock(Protocol.class);

	@Test
	void execute() {
		final int daysBack = 30;
		final int daysDeleted = 5;
		when(protocolService.protocol(CLEANUP_CALENDAR_BATCH_NAME)).thenReturn(protocol);
		when(configurationService.parameter(RuleKey.CleanUp, DaysBack, Integer.class)).thenReturn(Optional.of(daysBack));
		when(calendarService.deleteLocalDateDays(daysBack)).thenReturn(daysDeleted);

		cleanupCalendarService.execute(protocol);

		verify(calendarService).deleteLocalDateDays(daysBack);
		verify(protocolService).save(protocol);
		verify(protocolService).assignParameter(protocol, Configuration, DaysBack.name(), Optional.of(daysBack));
		verify(protocolService).assignParameter(protocol, Result, CleanupCalendarServiceImpl.RESULT_DAYS_DELETED, daysDeleted);
		verify(protocolService).success(protocol);
	}

	@Test
	void executeDaysBackEmpty() {
		Mockito.when(configurationService.parameter(CleanUp, DaysBack, Integer.class)).thenReturn(Optional.empty());
		when(protocolService.protocol(CLEANUP_CALENDAR_BATCH_NAME)).thenReturn(protocol);

		cleanupCalendarService.execute(protocol);

		verify(calendarService, Mockito.never()).deleteLocalDateDays(anyInt());
		verify(protocolService).save(protocol);
		verify(protocolService, times(1)).assignParameter(protocol, Configuration, DaysBack.name(), Optional.empty());
		verify(protocolService).success(protocol, CleanupCalendarServiceImpl.NOTHING_REMOVED);

	}

}
