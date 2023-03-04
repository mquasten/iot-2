package de.mq.iot2.main;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;

import de.mq.iot2.main.support.BatchMethod;
import de.mq.iot2.main.support.ReflectionCommandLineRunnerArgumentsImpl;
import de.mq.iot2.main.support.ScanUtil;
import de.mq.iot2.main.support.SimpleReflectionCommandLineRunner;

public abstract class Main {

	private static Logger LOG = LoggerFactory.getLogger(Main.class);

	public static final void main(final String[] args) throws Exception {
		process(args, SimpleReflectionCommandLineRunner.class);
	}

	static void process(final String[] args, final Class<?> primarySource) {
		final Map<String, Method> methods = ScanUtil.findBatchMethods(SimpleReflectionCommandLineRunner.COMPONENT_SCAN_BASE_PACKAGE);
		final var options = new Options();
		try {
			final var cmd = parser(options, args, methods.keySet());
			final var commandAsString = command(cmd, methods.keySet());
			final Method method = methods.get(commandAsString);
			final BatchMethod declaredAnnotation = method.getDeclaredAnnotation(BatchMethod.class);

			final Object[] convertedArgs = BeanUtils.instantiateClass(declaredAnnotation.converterClass()).convert(cmd.getArgList());

			ReflectionCommandLineRunnerArgumentsImpl commandLineRunnerArguments = new ReflectionCommandLineRunnerArgumentsImpl(method, convertedArgs);
			SpringApplication.run(primarySource, new Base64().encodeAsString(SerializationUtils.serialize(commandLineRunnerArguments)));
		} catch (final Exception exception) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar <file> [OPTION]... [ARGUMENT]...", options);
			System.err.println("\n" + exception.getMessage());
			LOG.error("Error executing batch:", exception);
		}
	}

	private static CommandLine parser(final Options options, final String[] args, Collection<String> commands) throws ParseException {
		options.addOption(
				Option.builder("c").hasArg().required().desc(String.format("command: %s", StringUtils.collectionToDelimitedString(commands, "|"))).argName("command").build());
		final var parser = new DefaultParser();
		return parser.parse(options, args);
	}

	private static final String command(final CommandLine cmd, Collection<String> commands) throws ParseException {
		final var command = cmd.getOptionValue("c");
		if (!commands.contains(command)) {
			throw new ParseException("Command is not defined.");
		}

		return cmd.getOptionValue("c");

	}

}
