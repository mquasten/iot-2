package de.mq.iot2.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Base64Utils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import de.mq.iot2.main.support.Batch;
import de.mq.iot2.main.support.Batch.BatchMethod;


@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = "de.mq.iot2")
@EnableTransactionManagement()
public class SpringBootConsoleApplication implements CommandLineRunner {

	
	private final Map<String, Batch> batches = new HashMap<>();
	private final Map<String, Class<? extends Converter<List<String>, Object[]>>> converters = new HashMap<>();
	private static final Collection<String>commands = List.of("setup" , "end-of-day");

	public SpringBootConsoleApplication(final Collection<Batch> beans) {
		
		beans.forEach(batch -> ReflectionUtils.doWithMethods(batch.getClass(), method ->  {
			final String batchname = method.getDeclaredAnnotation(BatchMethod.class).value();;
			batches.put(batchname, batch);
			converters.put(batchname,  method.getDeclaredAnnotation(BatchMethod.class).converterClass());
		}, method -> method.isAnnotationPresent(BatchMethod.class)));
		
		
	}

	public static final void main(final String[] args) throws Exception {
		final var options = new Options();
		try {

			final var cmd = parser(options, args);

			final var commandAsString = command(cmd);
			final var argsAsString = Base64Utils.encodeToString(SerializationUtils.serialize(cmd.getArgList()));

			SpringApplication.run(SpringBootConsoleApplication.class, commandAsString, argsAsString);

		} catch (final ParseException exception) {

			
				final HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java -jar <file> ", options);
			
			
		}
	}

	private static CommandLine parser(final Options options, final String[] args) throws ParseException {

		options.addOption("c", true, String.format("arg: %s",
				StringUtils.collectionToDelimitedString(commands, " ")));
		final var parser = new DefaultParser();

		return parser.parse(options, args);
	}

	private static final String command(final CommandLine cmd) throws ParseException {
		if (!cmd.hasOption("c")) {
			throw new ParseException("Command missing.");
		}
	    final var command=  cmd.getOptionValue("c");
		if( !commands.contains(command)) {
			throw new ParseException("Command is not defined.");
		}

		return cmd.getOptionValue("c");
		

	}

	@Override
	public void run(final String... args) throws Exception {
		final  String  command = args[0];
		@SuppressWarnings("unchecked")
		final List<String> argList = (List<String>) SerializationUtils
				.deserialize(Base64Utils.decodeFromString(args[1]));
		if (!batches.containsKey(command)) {
			throw new ParseException("No Bean found for command: " + command);
		}
		
		final Batch batch = batches.get(command);
		final Object[] objects = BeanUtils.instantiateClass(converters.get(command)).convert(argList);
		ReflectionUtils.doWithMethods(batch.getClass(), method -> executeMethod(objects, batch, method));

	}

	private void executeMethod(final Object[] argList, final Batch batch, Method method)
			throws IllegalAccessException {
		try {
			if (method.isAnnotationPresent(BatchMethod.class)) {
				method.setAccessible(true);
				method.invoke(batch, argList);
			}

		} catch (InvocationTargetException e) {
			ReflectionUtils.handleInvocationTargetException(e);
		}
	}


}
