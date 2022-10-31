package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import de.mq.iot2.calendar.CalendarService;

@SpringBootApplication
@ComponentScan(basePackages = "de.mq.iot2")
public class EndOfDayBatch implements CommandLineRunner {
	
	private final CalendarService calendarService;

	EndOfDayBatch(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	public final static boolean isValid(final CommandLine cmd) {
		if (cmd.getArgs().length == 0) {
			return true;
		}
		if (cmd.getArgs().length == 1) {
			return parseDate(cmd.getArgs()[0]);
		}
		return false;
	}

	private static boolean parseDate(final String dateString) {
		try {
			localDate(dateString);
			return true;
		} catch (DateTimeParseException ex) {
			return false;
		}

	}

	private static LocalDate localDate(final String dateString) {
		return LocalDate.parse(dateString, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN));
	}
	


	@Override
	public final void run(final String... args) throws Exception {
		final var date = args.length == 0 ? LocalDate.now().plusDays(1) : localDate(args[0]);
		System.out.println("Use date:" + date);
		
		calendarService.cycle(date);

	}

}
