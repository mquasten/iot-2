package de.mq.iot2.main;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;

import de.mq.iot2.main.support.EndOfDayBatch;
import de.mq.iot2.main.support.SetupDatabaseImpl;

public class SpringBootConsoleApplication {

	private final static Predicate<CommandLine> setupArgsValid = SetupDatabaseImpl::isValid;

	private final static Predicate<CommandLine> endOfDayArgsValid = EndOfDayBatch::isValid;

	private final static Map<String, Entry<Class<? extends CommandLineRunner>, Predicate<CommandLine>>> commands = Map.of("setup",
			new AbstractMap.SimpleImmutableEntry<>(SetupDatabaseImpl.class, setupArgsValid),

			"endOfDay", new AbstractMap.SimpleImmutableEntry<>(EndOfDayBatch.class, endOfDayArgsValid));

	private final static BiConsumer<Class<? extends CommandLineRunner>, String[]> consumer = (command, args) -> SpringApplication.run(command, args);

	public static final void main(final String[] args) {

		final var options = new Options();
		options.addOption("c", true, String.format("arg: %s", StringUtils.collectionToDelimitedString(commands.keySet(), " ")));
		final var parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		try {

			final var cmd = parser.parse(options, args);

			commandExistsGuard(commands.keySet(), cmd);
			if (commands.get(cmd.getOptionValue("c")).getValue().test(cmd)) {
				consumer.accept(commands.get(cmd.getOptionValue("c")).getKey(), cmd.getArgs());
			} else {
				throw new ParseException(String.format("Illegal number of Arguments."));
			}

		} catch (final ParseException parseException) {
			formatter.printHelp("java -jar <file> ", options);
			// System.exit(1);
		}
	}

	private static void commandExistsGuard(final Collection<String> commands, final CommandLine cmd) throws ParseException {
		if (!cmd.hasOption("c")) {
			throw new ParseException(String.format("Command missing."));
		}
		if (!commands.contains(cmd.getOptionValue("c"))) {
			throw new ParseException(String.format("Command undefined."));
		}
	}

}
