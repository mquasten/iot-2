package de.mq.iot2.main.support;

import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CleanupCalendarService;
import de.mq.iot2.protocol.CeanupProtocolService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

@Service
class CleanUpBatchImpl {
	static final String CLEANUP_CALENDAR_BATCH_NAME = "cleanup-calendar";
	static final String CLEANUP_PROTOCOL_BATCH_NAME = "cleanup-protocol";
	private final CleanupCalendarService cleanupCalendarService;
	private final CeanupProtocolService ceanupProtocolService;
	private final ProtocolService protocolService;

	CleanUpBatchImpl(final CleanupCalendarService cleanupCalendarService, CeanupProtocolService ceanupProtocolService, final ProtocolService protocolService) {
		this.cleanupCalendarService = cleanupCalendarService;
		this.ceanupProtocolService = ceanupProtocolService;
		this.protocolService=protocolService;
	}

	@BatchMethod(value = CLEANUP_CALENDAR_BATCH_NAME, converterClass = NoArgumentConverterImpl.class)
	final void cleanUpLocalDateDays() {
		final Protocol protocol = protocolService.protocol(CLEANUP_CALENDAR_BATCH_NAME);
		cleanupCalendarService.execute(protocol);
	}

	@BatchMethod(value = CLEANUP_PROTOCOL_BATCH_NAME, converterClass = NoArgumentConverterImpl.class)
	final void cleanUpProtocol() {
		final Protocol protocol = protocolService.protocol(CLEANUP_PROTOCOL_BATCH_NAME);
		ceanupProtocolService.execute(protocol);
	}
	
	
	
	
	
	
	
	
}
