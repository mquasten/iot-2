package de.mq.iot2.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Base64Utils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import de.mq.iot2.main.support.BatchMethod;
import de.mq.iot2.main.support.ScanUtil;



@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = SpringBootConsoleApplication.COMPONENT_SCAN_BASE_PACKAGE)
@EnableTransactionManagement()
public class SpringBootConsoleApplication implements CommandLineRunner {

	static final String COMPONENT_SCAN_BASE_PACKAGE = "de.mq.iot2";
	private final ApplicationContext applicationContext;
	SpringBootConsoleApplication(ApplicationContext applicationContext){
		this.applicationContext=applicationContext;
	}

	public static final void main(final String[] args) throws Exception {
		final Map<String, Method> methods = ScanUtil.findBatchMethods(COMPONENT_SCAN_BASE_PACKAGE);
		final var options = new Options();
		try {

			final var cmd = parser(options, args, methods.keySet());

			final var commandAsString = command(cmd, methods.keySet());
			//final var argsAsString = Base64Utils.encodeToString(SerializationUtils.serialize(cmd.getArgList()));

			final var converter = BeanUtils.instantiateClass(methods.get(commandAsString).getDeclaredAnnotation(BatchMethod.class).converterClass());
			final Object[] convertedArgs = converter.convert(cmd.getArgList());
			final var argsAsString = Base64Utils.encodeToString(SerializationUtils.serialize(convertedArgs));
			
			SpringApplication.run(SpringBootConsoleApplication.class, commandAsString, argsAsString);

		} catch (final ParseException exception) {

			
				final HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java -jar <file> ", options);
			
			
		}
	}

	private static CommandLine parser(final Options options, final String[] args, Collection<String> commands) throws ParseException {

		options.addOption("c", true, String.format("arg: %s",
				StringUtils.collectionToDelimitedString(commands, " ")));
		final var parser = new DefaultParser();

		return parser.parse(options, args);
	}

	private static final String command(final CommandLine cmd, Collection<String> commands) throws ParseException {
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
		final Map<String, Method> methods = ScanUtil.findBatchMethods(COMPONENT_SCAN_BASE_PACKAGE);
		final  String  command = args[0];
	
		final Object[]  argList = (Object[]) SerializationUtils
				.deserialize(Base64Utils.decodeFromString(args[1]));
		if (!methods.containsKey(command)) {
			throw new ParseException("No Bean found for command: " + command);
		}
		
		final Method method = methods.get(command);
		
		
		executeMethod(argList, applicationContext.getBean(method.getDeclaringClass()), method);
		

	}

	private void executeMethod(final Object[] argList, final Object bean, Method method)
			throws IllegalAccessException {
		try {
			if (method.isAnnotationPresent(BatchMethod.class)) {
				method.setAccessible(true);
				method.invoke(bean, argList);
			}

		} catch (InvocationTargetException e) {
			ReflectionUtils.handleInvocationTargetException(e);
		}
	}


}
