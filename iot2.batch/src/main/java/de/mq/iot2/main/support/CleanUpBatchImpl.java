package de.mq.iot2.main.support;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;

@Service
class CleanUpBatchImpl {
	static final String CLEANUP_CALENDAR_BATCH_NAME = "cleanup-calendar";
	static final String CLEANUP_PROTOCOL_BATCH_NAME = "cleanup-protocol";
	static final String RESULT_DAYS_DELETED = "DaysDeleted";
	static final String RESULT_PROTOCOLS_DELETED = "ProtocolsDeleted";
	static final String NOTHING_REMOVED = "Configuration for cleanup is missing. Nothing will be deleted.";
	private static Logger LOGGER = LoggerFactory.getLogger(CleanUpBatchImpl.class);
	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	private final ProtocolService protocolService;

	CleanUpBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService, final ProtocolService protocolService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
		this.protocolService=protocolService;
	}

	@BatchMethod(value = CLEANUP_CALENDAR_BATCH_NAME, converterClass = NoArgumentConverterImpl.class)
	final void cleanUpLocalDateDays() {
		final Protocol protocol = protocolService.protocol(CLEANUP_CALENDAR_BATCH_NAME);
		try {
			cleanUpLocalDateDays(protocol);
		} catch (final RuntimeException exception) {
			protocolService.error(protocol, exception);
			throw exception;
		}
	}

	private void cleanUpLocalDateDays(final Protocol protocol) {
		protocolService.save(protocol);
		final Optional<Integer> daysBack = configurationService.parameter(RuleKey.CleanUp, Key.DaysBack, Integer.class);
		protocolService.assignParameter(protocol, ProtocolParameterType.Configuration, Key.DaysBack.name(), daysBack);
		if (daysBack.isEmpty()) {
			LOGGER.warn(NOTHING_REMOVED);
			protocolService.success(protocol, NOTHING_REMOVED );
			return;
		}		
		
		LOGGER.info("Delete localDateDays elder or equals {} days back.", daysBack.get());
		final var numberOfDaysDeleted = calendarService.deleteLocalDateDays(daysBack.get());
		LOGGER.info("{} days deleted.", numberOfDaysDeleted);
		protocolService.assignParameter(protocol, ProtocolParameterType.Result, RESULT_DAYS_DELETED, numberOfDaysDeleted);
		protocolService.success(protocol);
	}
	
	
	@BatchMethod(value = CLEANUP_PROTOCOL_BATCH_NAME, converterClass = NoArgumentConverterImpl.class)
	final void cleanUpProtocol() {
		final Protocol protocol = protocolService.protocol(CLEANUP_PROTOCOL_BATCH_NAME);
		try {
			cleanUpProtocol(protocol);
		} catch (final RuntimeException exception) {
			protocolService.error(protocol, exception);
			throw exception;
		}
	}
	
	private void cleanUpProtocol(final Protocol protocol) {
		protocolService.save(protocol);
		final Optional<Integer> daysBack = configurationService.parameter(RuleKey.CleanUp, Key.ProtocolBack, Integer.class);
		protocolService.assignParameter(protocol, ProtocolParameterType.Configuration, Key.ProtocolBack.name(), daysBack);
		if (daysBack.isEmpty()) {
			LOGGER.warn(NOTHING_REMOVED);
			protocolService.success(protocol, NOTHING_REMOVED );
			return;
		}		
		
		LOGGER.info("Delete protocols elder or equals {} days back.", daysBack.get());
		final var numberOfProtocolsDeleted = protocolService.deleteProtocols(daysBack.get());
		LOGGER.info("{} protocols deleted.", numberOfProtocolsDeleted);
		protocolService.assignParameter(protocol, ProtocolParameterType.Result, RESULT_PROTOCOLS_DELETED, numberOfProtocolsDeleted);
		protocolService.success(protocol);
	}
	
	
	
	
	
	
}
