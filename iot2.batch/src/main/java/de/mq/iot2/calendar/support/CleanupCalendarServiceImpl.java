package de.mq.iot2.calendar.support;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CleanupCalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;

@Service
class CleanupCalendarServiceImpl implements CleanupCalendarService {

	static final String RESULT_DAYS_DELETED = "DaysDeleted";
	static final String RESULT_PROTOCOLS_DELETED = "ProtocolsDeleted";
	static final String NOTHING_REMOVED = "Configuration for cleanup is missing. Nothing will be deleted.";
	private static Logger LOGGER = LoggerFactory.getLogger(CleanupCalendarServiceImpl.class);
	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	private final ProtocolService protocolService;

	CleanupCalendarServiceImpl(final CalendarService calendarService, final ConfigurationService configurationService, final ProtocolService protocolService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
		this.protocolService = protocolService;
	}

	@Override
	public void execute(final Protocol protocol) {
		protocolService.save(protocol);
		final Optional<Integer> daysBack = configurationService.parameter(RuleKey.CleanUp, Key.DaysBack, Integer.class);
		protocolService.assignParameter(protocol, ProtocolParameterType.Configuration, Key.DaysBack.name(), daysBack);
		if (daysBack.isEmpty()) {
			LOGGER.warn(NOTHING_REMOVED);
			protocolService.success(protocol, NOTHING_REMOVED);
			return;
		}

		LOGGER.info("Delete localDateDays elder or equals {} days back.", daysBack.get());
		final var numberOfDaysDeleted = calendarService.deleteLocalDateDays(daysBack.get());
		LOGGER.info("{} days deleted.", numberOfDaysDeleted);
		protocolService.assignParameter(protocol, ProtocolParameterType.Result, RESULT_DAYS_DELETED, numberOfDaysDeleted);
		protocolService.success(protocol);

	}

}
