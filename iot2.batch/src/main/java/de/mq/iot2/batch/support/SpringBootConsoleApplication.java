package de.mq.iot2.batch.support;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;


public class SpringBootConsoleApplication  {

	
	
	private final  static Map<String,Class<? extends CommandLineRunner>> commands = Map.of("setup", SetupDatabaseImpl.class);

	public static void main(String[] args) {
		
		final var options = new Options();
		options.addOption("c", "command", true, String.format("arg: %s" , StringUtils.collectionToDelimitedString(commands.keySet(), " ")));
		final var parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {

			final var  cmd = parser.parse(options, args);
		
			commandExistsGuard(commands.keySet(), cmd);
			
			SpringApplication.run(commands.get(cmd.getOptionValue("c")));
			

		} catch (final ParseException parseException) {

			//parseException.printStackTrace();
			formatter.printHelp("java -jar <file> ", options);
			System.exit(1);
		}

		
	}

	private static void commandExistsGuard(final Collection<String> commands, final CommandLine cmd)
			throws ParseException {
		if (!cmd.hasOption("c")) {
			throw new ParseException( String.format("Command missing.")); 
		}
		if (!commands.contains(cmd.getOptionValue("c"))) {

			throw new ParseException( String.format("Command undefined.")); 
		}
	}

	

}
