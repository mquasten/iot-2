package de.mq.iot2.main;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;

import de.mq.iot2.main.support.EndOfDayBatchImpl;
import de.mq.iot2.main.support.ReflectionCommandLineRunnerArgumentsImpl;

class MainTest {

	private final static List<String> ARGUMENTS = new ArrayList<>();

	@BeforeEach
	@AfterEach
	void clearList() {
		ARGUMENTS.clear();
	}

	@Test
	void main() throws Exception {

		Main.process(new String[] { "-cend-of-day", "13.06.1831" }, ((CommandLineRunner) args -> ARGUMENTS.add(args[0])).getClass());

		assertEquals(1, ARGUMENTS.size());
		final var reflectionCommandLineRunnerArguments = (ReflectionCommandLineRunnerArgumentsImpl) SerializationUtils
				.deserialize(new Base64().decode(ARGUMENTS.stream().findFirst().get()));
		assertEquals(LocalDate.of(1831, 6, 13), reflectionCommandLineRunnerArguments.getParameterValues()[0]);
		assertEquals(EndOfDayBatchImpl.class, reflectionCommandLineRunnerArguments.getExecutedBean());
		assertEquals("execute", reflectionCommandLineRunnerArguments.getMethodName());
		assertArrayEquals(new Class[] { LocalDate.class }, reflectionCommandLineRunnerArguments.getParameterTypes());

	}

	@Test
	void mainUnkownCommandOption() throws Exception {
		Main.main(new String[] { "-cunkown", "xxx" });
		assertEquals(0, ARGUMENTS.size());
	}

	@Test
	void mainMissingCommandOptionValue() throws Exception {
		Main.main(new String[] { "-c", "xxx" });
		assertEquals(0, ARGUMENTS.size());
	}

	@Test
	void coverage() {
		Main consoleApplication = Mockito.mock(Main.class);
		assertNotNull(BeanUtils.instantiateClass(consoleApplication.getClass()));
	}

}
