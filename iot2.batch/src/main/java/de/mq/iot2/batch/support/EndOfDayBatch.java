package de.mq.iot2.batch.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = "de.mq.iot2")
@EnableTransactionManagement()
public class EndOfDayBatch  implements CommandLineRunner{

	@Override
	public void run(final String... args) throws Exception {
		System.out.println("EndOfDay");
		
	}
	
	final static boolean isValid(final CommandLine cmd) {
		if(  cmd.getArgs().length==0) {
			return true;
		}
		if (cmd.getArgs().length == 1) {
			 return parseDate(cmd.getArgs()[0]);
		}
		return false;
	}

	private static boolean  parseDate(final String dateString) {
		try {
		LocalDate.parse(dateString, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN));
		return true;
		} catch ( DateTimeParseException ex) {
			return false;
		}
		
	}

}
