package de.mq.iot2.main.support;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.main.SpringBootConsoleApplication;


@Service
public class SetupDatabase  {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	private final CalendarService calendarService;
	private final ConfigurationService configurationService;

	SetupDatabase(final CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
	}

	public final void execute()  {
		LOG.info("Setup database DayGroups and Days.");
		calendarService.createDefaultCyclesGroupsAndDays();
		configurationService.createDefaultConfigurationsAndParameters();

	}

	final public static boolean isValid(final CommandLine cmd) {
		return cmd.getArgs().length == 0;
	}

}
