package de.mq.iot2.batch.support;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.CalendarService;

@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = "de.mq.iot2")
@EnableTransactionManagement()
public class SpringBootConsoleApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	private final CalendarService calendarService;

	SpringBootConsoleApplication(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	public static void main(String[] args) {
		final Collection<String> commands = Arrays.asList("setup");
		Options options = new Options();
		options.addOption("c", true, StringUtils.collectionToDelimitedString(commands, " "));
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {

			CommandLine cmd = parser.parse(options, args);
			if (!commands.contains(cmd.getOptionValue("c"))) {
				formatter.printHelp("iot", options);
				System.exit(1);
			}
			SpringApplication.run(SpringBootConsoleApplication.class, args);

		} catch (final ParseException parseException) {

			formatter.printHelp("ant", options);
			System.exit(1);
		}

		LOG.info("STARTING THE APPLICATION");

		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {

		LOG.info("EXECUTING : command line runner");

		calendarService.createDefaultGroupsAndDays();

	}

}
