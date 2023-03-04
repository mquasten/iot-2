package de.mq.iot2.main.support;

import java.lang.reflect.Method;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = SimpleReflectionCommandLineRunner.COMPONENT_SCAN_BASE_PACKAGE)
@EnableTransactionManagement()
public class SimpleReflectionCommandLineRunner implements CommandLineRunner {

	public static final String COMPONENT_SCAN_BASE_PACKAGE = "de.mq.iot2";
	private final ApplicationContext applicationContext;

	SimpleReflectionCommandLineRunner(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public final void run(final String... args) throws Exception {
		final var arguments = (ReflectionCommandLineRunnerArgumentsImpl) SerializationUtils.deserialize(new Base64().decode(args[0]));

		final var bean = applicationContext.getBean(arguments.getExecutedBean());
		final Method method = ReflectionUtils.findMethod(arguments.getExecutedBean(), arguments.getMethodName(), arguments.getParameterTypes());
		Assert.notNull(method, "Method not found.");
		method.setAccessible(true);
		ReflectionUtils.invokeMethod(method, bean, arguments.getParameterValues());

	}

}
