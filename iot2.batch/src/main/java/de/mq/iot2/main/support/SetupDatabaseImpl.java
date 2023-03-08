package de.mq.iot2.main.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.main.Main;

@Service
class SetupDatabaseImpl {

	private static Logger LOG = LoggerFactory.getLogger(Main.class);

	private final CalendarService calendarService;
	private final ConfigurationService configurationService;

	SetupDatabaseImpl(final CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
	}

	@BatchMethod("setup")
	final void execute() {
		LOG.info("Setup database: DayGroups, days, cycles, configurations and parameters.");
		calendarService.createDefaultCyclesGroupsAndDays();
		configurationService.createDefaultConfigurationsAndParameters();
		LOG.info("Setup database finished");
	}

}
