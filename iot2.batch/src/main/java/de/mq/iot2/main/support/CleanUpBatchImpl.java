package de.mq.iot2.main.support;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;

@Service
class CleanUpBatchImpl {

	private static Logger LOGGER = LoggerFactory.getLogger(CleanUpBatchImpl.class);
	private final CalendarService calendarService;
	private final ConfigurationService configurationService;

	CleanUpBatchImpl(CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
	}

	@BatchMethod(value = "cleanup", converterClass = NoArgumentConverterImpl.class)
	final void cleanUpLocalDateDays() {
		final Optional<Integer> daysBack = configurationService.parameter(RuleKey.CleanUp, Key.DaysBack);
		if(daysBack.isEmpty()) {
			LOGGER.warn("Configuration for cleanup is missing. Nothing will be deleted.");
			return;
		}
		
		LOGGER.info("Delete localDateDays elder or equals {} days back.", daysBack.get());
		final var numberOfDaysDeleted = calendarService.deleteLocalDateDays(daysBack.get());
		LOGGER.info("{} days deleted.", numberOfDaysDeleted);
	}

}
