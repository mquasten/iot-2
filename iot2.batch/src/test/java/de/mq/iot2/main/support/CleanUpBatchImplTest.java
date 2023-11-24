package de.mq.iot2.main.support;

import static de.mq.iot2.main.support.CleanUpBatchImpl.CLEANUP_CALENDAR_BATCH_NAME;
import static de.mq.iot2.main.support.CleanUpBatchImpl.CLEANUP_PROTOCOL_BATCH_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CleanupCalendarService;
import de.mq.iot2.protocol.CeanupProtocolService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

class CleanUpBatchImplTest {

	private final CleanupCalendarService cleanupCalendarService = mock(CleanupCalendarService.class);
	private final CeanupProtocolService ceanupProtocolService = mock(CeanupProtocolService.class);
	private final ProtocolService protocolService = mock(ProtocolService.class);
	private final CleanUpBatchImpl cleanUpBatch = new CleanUpBatchImpl(cleanupCalendarService, ceanupProtocolService, protocolService);
	private final Protocol protocol = mock(Protocol.class);

	@Test
	void cleanUpLocalDateDays() {
		Mockito.when(protocolService.protocol(CleanUpBatchImpl.CLEANUP_CALENDAR_BATCH_NAME)).thenReturn(protocol);

		cleanUpBatch.cleanUpLocalDateDays();

		verify(cleanupCalendarService).execute(protocol);
		verify(protocolService).protocol(CLEANUP_CALENDAR_BATCH_NAME);
		verify(ceanupProtocolService, Mockito.never()).execute(Mockito.any());
	}

	@Test
	void cleanUpProtocol() {
		Mockito.when(protocolService.protocol(CLEANUP_PROTOCOL_BATCH_NAME)).thenReturn(protocol);

		cleanUpBatch.cleanUpProtocol();

		verify(ceanupProtocolService).execute(protocol);
		verify(protocolService).protocol(CLEANUP_PROTOCOL_BATCH_NAME);
		verify(cleanupCalendarService, Mockito.never()).execute(Mockito.any());

	}

}
