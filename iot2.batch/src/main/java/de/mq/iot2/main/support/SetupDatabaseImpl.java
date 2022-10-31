package de.mq.iot2.main.support;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.main.SpringBootConsoleApplication;

@SpringBootApplication
@ComponentScan(basePackages = "de.mq.iot2")
public class SetupDatabaseImpl implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	private final CalendarService calendarService;

	SetupDatabaseImpl(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	@Override
	public final void run(final String... args) throws Exception {
		LOG.info("Setup database DayGroups and Days.");
		calendarService.createDefaultCyclesGroupsAndDays();

	}

	final public static boolean isValid(final CommandLine cmd) {
		return cmd.getArgs().length == 0;
	}

}
