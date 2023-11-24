package de.mq.iot2.main.support;

import static de.mq.iot2.main.support.EndOfDayBatchImpl.END_OF_DAY_BATCH_NAME;
import static de.mq.iot2.main.support.EndOfDayBatchImpl.END_OF_DAY_UPDATE_BATCH_NAME;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.EndOfDayService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

class EndOfDayBatchImplTest {

	private final ProtocolService protocolService = Mockito.mock(ProtocolService.class);

	private final EndOfDayService endOfDayService = Mockito.mock(EndOfDayService.class);
	private final Protocol protocol = Mockito.mock(Protocol.class);
	private final LocalDate date = LocalDate.now().plusDays(1);

	private final EndOfDayBatchImpl endOfDayBatch = new EndOfDayBatchImpl(endOfDayService, protocolService);

	@Test
	final void execute() {

		Mockito.when(protocolService.protocol(END_OF_DAY_BATCH_NAME)).thenReturn(protocol);

		endOfDayBatch.execute(date);

		verify(endOfDayService).execute(protocol, date, Optional.empty());
		verify(protocolService).protocol(END_OF_DAY_BATCH_NAME);

	}

	@Test
	final void executeUpdate() {
		final var date = LocalDate.now();
		final var time = LocalTime.of(11, 11);
		Mockito.when(protocolService.protocol(END_OF_DAY_UPDATE_BATCH_NAME)).thenReturn(protocol);

		endOfDayBatch.executeUpdate(time);

		verify(endOfDayService).execute(protocol, date, Optional.of(time));
		verify(protocolService).protocol(END_OF_DAY_UPDATE_BATCH_NAME);
	}

}
