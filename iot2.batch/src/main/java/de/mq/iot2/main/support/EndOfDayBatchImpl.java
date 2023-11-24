package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.EndOfDayService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

@Service
public class EndOfDayBatchImpl {
	static final String END_OF_DAY_UPDATE_BATCH_NAME = "end-of-day-update";
	static final String END_OF_DAY_BATCH_NAME = "end-of-day";

	private final EndOfDayService endOfDayService;
	private final ProtocolService protocolService;

	EndOfDayBatchImpl(final EndOfDayService endOfDayService, final ProtocolService protocolService) {
		this.endOfDayService = endOfDayService;
		this.protocolService = protocolService;

	}

	@BatchMethod(value = END_OF_DAY_BATCH_NAME, converterClass = EndOfDayBatchArgumentConverterImpl.class)
	final void execute(final LocalDate date) {
		final Protocol protocol = protocolService.protocol(END_OF_DAY_BATCH_NAME);
		endOfDayService.execute(protocol, date, Optional.empty());
	}

	@BatchMethod(value = END_OF_DAY_UPDATE_BATCH_NAME, converterClass = EndOfDayUpdateBatchArgumentConverterImpl.class)
	final void executeUpdate(final LocalTime time) {
		final Protocol protocol = protocolService.protocol(END_OF_DAY_UPDATE_BATCH_NAME);
		endOfDayService.execute(protocol, LocalDate.now(), Optional.of(time));
	}

}
