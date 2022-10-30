package de.mq.iot2.batch.support;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;

public class SpringBootConsoleApplication {

	private final static Map<String, Entry<Class<? extends CommandLineRunner>, Integer>> commands = Map.of("setup", new AbstractMap.SimpleImmutableEntry<>(SetupDatabaseImpl.class, 0));

	private final static BiConsumer<Class<? extends CommandLineRunner>, String[]> consumer = (command, args) -> SpringApplication.run(command, args);

	public static final void main(final String[] args) {

		final var options = new Options();
		options.addOption("c", "command", true, String.format("arg: %s", StringUtils.collectionToDelimitedString(commands.keySet(), " ")));
		final var parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		try {

			final var cmd = parser.parse(options, args);

			commandExistsGuard(commands.keySet(), cmd);
			numberOfArgumentsGuard(cmd);

			consumer.accept(commands.get(cmd.getOptionValue("c")).getKey(), cmd.getArgs());

		} catch (final ParseException parseException) {
			formatter.printHelp("java -jar <file> ", options);
			// System.exit(1);
		}
	}

	private static void numberOfArgumentsGuard(final CommandLine cmd) throws ParseException {
		if (cmd.getArgs().length != commands.get(cmd.getOptionValue("c")).getValue()) {
			throw new ParseException(String.format("Invalid number of Arguments."));
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
