package de.mq.iot2.main;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;

import de.mq.iot2.main.support.EndOfDayBatchImpl;
import de.mq.iot2.main.support.ReflectionCommandLineRunnerArgumentsImpl;

class SpringBootConsoleApplicationTest {

	private final static List<String> ARGUMENTS = new ArrayList<>();

	@BeforeEach
	@AfterEach
	void clearList() {
		ARGUMENTS.clear();
	}

	@Test
	void main() throws Exception {

		AbstractSpringBootConsoleApplication.process(new String[] { "-cend-of-day", "13.06.1831" }, ((CommandLineRunner) args -> ARGUMENTS.add(args[0])).getClass());

		assertEquals(1, ARGUMENTS.size());
		final var reflectionCommandLineRunnerArguments = (ReflectionCommandLineRunnerArgumentsImpl) SerializationUtils.deserialize(Base64Utils.decodeFromString(ARGUMENTS.stream().findFirst().get()));
		assertEquals(LocalDate.of(1831, 6, 13), reflectionCommandLineRunnerArguments.getParameterValues()[0]);
		assertEquals(EndOfDayBatchImpl.class, reflectionCommandLineRunnerArguments.getExecutedBean());
		assertEquals("execute", reflectionCommandLineRunnerArguments.getMethodName());
		assertArrayEquals(new Class[] { LocalDate.class }, reflectionCommandLineRunnerArguments.getParameterTypes());

	}

	@Test
	void mainUnkownCommandOption() throws Exception {
		AbstractSpringBootConsoleApplication.main(new String[] { "-cunkown", "xxx" });
		assertEquals(0, ARGUMENTS.size());
	}

	@Test
	void mainMissingCommandOptionValue() throws Exception {
		AbstractSpringBootConsoleApplication.main(new String[] { "-c", "xxx" });
		assertEquals(0, ARGUMENTS.size());
	}

	@Test
	void coverage() {
		AbstractSpringBootConsoleApplication consoleApplication = Mockito.mock(AbstractSpringBootConsoleApplication.class);
		assertNotNull(BeanUtils.instantiateClass(consoleApplication.getClass()));
	}

}
