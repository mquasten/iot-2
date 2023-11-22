package de.mq.iot2.main.support;

import static de.mq.iot2.configuration.Configuration.RuleKey.CleanUp;
import static de.mq.iot2.configuration.Parameter.Key.DaysBack;
import static de.mq.iot2.main.support.CleanUpBatchImpl.CLEANUP_BATCH_NAME;
import static de.mq.iot2.main.support.CleanUpBatchImpl.NOTHING_REMOVED;
import static de.mq.iot2.main.support.CleanUpBatchImpl.RESULT_DAYS_DELETED;
import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Configuration;
import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

class CleanUpBatchImplTest {

	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	private final CalendarService calendarService = mock(CalendarService.class);
	private final ProtocolService protocolService = mock(ProtocolService.class);
	private final CleanUpBatchImpl cleanUpBatch = new CleanUpBatchImpl(calendarService, configurationService, protocolService);
	private final Protocol protocol = mock(Protocol.class);

	@Test
	void cleanUpLocalDateDays() {
		final int daysBack = 30;
		final int daysDeleted=5;
		when(protocolService.protocol(CleanUpBatchImpl.CLEANUP_BATCH_NAME)).thenReturn(protocol);
		when(configurationService.parameter(RuleKey.CleanUp, DaysBack, Integer.class)).thenReturn(Optional.of(daysBack));
		when(calendarService.deleteLocalDateDays(daysBack)).thenReturn(daysDeleted);
		
		cleanUpBatch.cleanUpLocalDateDays();

		verify(calendarService).deleteLocalDateDays(daysBack);
		verify(protocolService).save(protocol);
		verify(protocolService).assignParameter(protocol, Configuration, DaysBack.name(),Optional.of(daysBack));
		verify(protocolService).assignParameter(protocol, Result, RESULT_DAYS_DELETED, daysDeleted);
		verify(protocolService).success(protocol);
	}

	@Test
	void cleanUpLocalDateDaysDaysBackEmpty() {
		Mockito.when(configurationService.parameter(CleanUp, DaysBack, Integer.class)).thenReturn(Optional.empty());
		when(protocolService.protocol(CLEANUP_BATCH_NAME)).thenReturn(protocol);
		
		cleanUpBatch.cleanUpLocalDateDays();

		verify(calendarService, Mockito.never()).deleteLocalDateDays(anyInt());
		verify(protocolService).save(protocol);
		verify(protocolService, times(1)).assignParameter(protocol, Configuration, DaysBack.name(),Optional.empty());
		verify(protocolService).success(protocol, NOTHING_REMOVED);
	
	}
	
	@Test
	void cleanUpLocalDateDaysException() {
		final int daysBack = 30;
		when(protocolService.protocol(CLEANUP_BATCH_NAME)).thenReturn(protocol);
		when(configurationService.parameter(CleanUp, DaysBack, Integer.class)).thenReturn(Optional.of(daysBack));
		final Throwable exception = new IllegalStateException();
		when(calendarService.deleteLocalDateDays(anyInt())).thenThrow(exception);
		
		assertThrows(IllegalStateException.class, () ->  cleanUpBatch.cleanUpLocalDateDays());
		
		verify(protocolService).save(protocol);
		verify(protocolService).assignParameter(protocol, Configuration, DaysBack.name(),Optional.of(daysBack));
		verify(protocolService).error(protocol, exception);
	}

}
