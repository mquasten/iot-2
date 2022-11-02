package de.mq.iot2.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import de.mq.iot2.main.support.EndOfDayBatch;
import de.mq.iot2.main.support.SetupDatabase;




@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = "de.mq.iot2")
@EnableTransactionManagement()
public class SpringBootConsoleApplication implements CommandLineRunner {
	
	public enum Commands  {
		Setup,
		EndOfDay;
	}

	private final EndOfDayBatch endOfDayBatch;
	private final SetupDatabase setupDatabase;
	public SpringBootConsoleApplication(final EndOfDayBatch endOfDayBatch, final SetupDatabase setupDatabase) {
		this.endOfDayBatch = endOfDayBatch;
		this.setupDatabase = setupDatabase;
	}

	

	public static final void main(final String[] args) throws Exception {
		final var options = new Options();
		try {

			final var cmd = parser(options, args);

			final var commandAsString= Base64Utils.encodeToString(SerializationUtils.serialize(command(cmd)));
			final var argsAsString = Base64Utils.encodeToString(SerializationUtils.serialize(cmd.getArgList()));
			
			SpringApplication.run(SpringBootConsoleApplication.class, commandAsString, argsAsString);

		} catch (final Exception  exception) {
			
			if ((exception instanceof ParseException) || (exception.getCause() instanceof ParseException )) {
				final HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java -jar <file> ", options);
				return;
				
			}
	
			throw exception;
		}
	}

	private static CommandLine parser(final Options options, final String[] args) throws ParseException {

		options.addOption("c", true, String.format("arg: %s", StringUtils.collectionToDelimitedString(Arrays.asList(Commands.values()), " ")));
		final var parser = new DefaultParser();

		return parser.parse(options, args);
	}

	private static Commands command(final CommandLine cmd)
			throws ParseException {
		if (!cmd.hasOption("c")) {
			throw new ParseException("Command missing.");
		}
		try {
			
		return Commands.valueOf(cmd.getOptionValue("c"));
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Command undefined.");
		}
		
	}

	@Override
	public void run(String... args) throws Exception {
		final Commands command = (Commands) SerializationUtils.deserialize(Base64Utils.decodeFromString(args[0]));
		@SuppressWarnings("unchecked")
		final List<String> argList =  (List<String>)  SerializationUtils.deserialize(Base64Utils.decodeFromString(args[1]));
		System.out.println("***" + command+ ", Argumente: " + argList.size() + "****");
		
		switch (command) {
		case Setup: {
			setupDatabase.execute();
			
		}
		case EndOfDay: {
			endOfDayBatch.execute(argList.isEmpty() ? Optional.empty() :  Optional.of(localDate(argList.get(0))) );
		}
		
		
		

	}
		
	}
	
	private  LocalDate localDate(final String dateString) {
		return LocalDate.parse(dateString, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN));
	}

}
