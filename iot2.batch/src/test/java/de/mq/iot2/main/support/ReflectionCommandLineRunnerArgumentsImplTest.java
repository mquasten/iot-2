package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

class ReflectionCommandLineRunnerArgumentsImplTest {

	private static final String METHODE_NAME = "execute";
	private final ReflectionCommandLineRunnerArgumentsImpl reflectionCommandLineRunnerArguments = new ReflectionCommandLineRunnerArgumentsImpl();

	@Test
	void executedBean() {
		assertNull(reflectionCommandLineRunnerArguments.getExecutedBean());

		reflectionCommandLineRunnerArguments.setExecutedBean(EndOfDayBatchImpl.class);

		assertEquals(EndOfDayBatchImpl.class, reflectionCommandLineRunnerArguments.getExecutedBean());
	}

	@Test
	void methodName() {
		assertNull(reflectionCommandLineRunnerArguments.getMethodName());

		reflectionCommandLineRunnerArguments.setMethodName(METHODE_NAME);
		assertEquals(METHODE_NAME, reflectionCommandLineRunnerArguments.getMethodName());
	}

	@Test
	void parameterTypes() {
		assertNull(reflectionCommandLineRunnerArguments.getParameterTypes());

		final var parameterTypes = new Class[] { LocalDate.class };
		reflectionCommandLineRunnerArguments.setParameterTypes(parameterTypes);
		assertEquals(parameterTypes, reflectionCommandLineRunnerArguments.getParameterTypes());
	}

	@Test
	void parameterValues() {
		assertNull(reflectionCommandLineRunnerArguments.getParameterValues());

		final var parameterValues = new Object[] { LocalDate.now() };
		reflectionCommandLineRunnerArguments.setParameterValues(parameterValues);
		assertEquals(parameterValues, reflectionCommandLineRunnerArguments.getParameterValues());
	}

	@Test
	void createWithArguments() {
		final var method = ReflectionUtils.findMethod(EndOfDayBatchImpl.class, METHODE_NAME, LocalDate.class);
		final var parameterValues = new Object[] { LocalDate.now() };
		final var reflectionCommandLineRunnerArguments = new ReflectionCommandLineRunnerArgumentsImpl(method, parameterValues);

		assertEquals(parameterValues, reflectionCommandLineRunnerArguments.getParameterValues());
		assertEquals(METHODE_NAME, reflectionCommandLineRunnerArguments.getMethodName());
		assertEquals(method.getDeclaringClass(), reflectionCommandLineRunnerArguments.getExecutedBean());
		assertEquals(method.getParameterTypes().length, reflectionCommandLineRunnerArguments.getParameterTypes().length);
		assertArrayEquals(method.getParameterTypes(), reflectionCommandLineRunnerArguments.getParameterTypes());
	}

	@Test
	void createWithArgumentsNumberDifferent() {
		final var method = ReflectionUtils.findMethod(SetupDatabaseImpl.class, METHODE_NAME);
		assertThrows(IllegalStateException.class, () -> new ReflectionCommandLineRunnerArgumentsImpl(method, new Object[] { LocalDate.now() }));
	}

	@Test
	void serializable() {
		assertTrue(reflectionCommandLineRunnerArguments instanceof Serializable);
	}

}
