package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class AbstractDayTest {

	private static final String VALUE_FIELD_NAME = "value";
	private static final String DESCRIPTION = "description";
	private static final int TYPE = 4711;

	@SuppressWarnings("unchecked")
	AbstractDay<Integer> newAbstractDay(int[] values, int[] digits) {
		try {
			return BeanUtils.instantiateClass(((AbstractDay<Integer>) Mockito.mock(AbstractDay.class)).getClass().getDeclaredConstructor(int[].class, int[].class, int.class, String.class), values,
					digits, TYPE, DESCRIPTION);
		} catch (final Exception exception) {
			throw runtimeExceptionn(exception);
		}
	}

	private RuntimeException runtimeExceptionn(final Exception exception) {
		if (exception.getCause() instanceof RuntimeException) {
			return (RuntimeException) exception.getCause();
		}
		return new IllegalStateException("Unable to create exception AbstractDay", exception);
	}

	@Test
	void create() {
		final var day = newAbstractDay(new int[] { 1968, 5, 28 }, new int[] { 4, 2, 2 });
		assertTrue(day.description().isPresent());
		assertEquals(Optional.of(DESCRIPTION), day.description());
		final var expectedValue = 19680528;
		assertEquals(expectedValue, ReflectionTestUtils.getField(day, VALUE_FIELD_NAME));
		assertEquals(new UUID(TYPE, expectedValue).toString(), ReflectionTestUtils.getField(day, "id"));
	}

	@ParameterizedTest
	@MethodSource("invalidConstructorArgs")
	void createInvalidValues(final Entry<int[], int[]> values) {
		assertThrows(IllegalArgumentException.class, () -> newAbstractDay(values.getKey(), values.getValue()));
	}

	@Test
	void createNullValues() {
		assertThrows(IllegalArgumentException.class, () -> newAbstractDay(null, new int[] { 1 }));
		assertThrows(IllegalArgumentException.class, () -> newAbstractDay(new int[] { 1 }, null));
	}

	private static Collection<Entry<int[], int[]>> invalidConstructorArgs() {
		return Map.of(new int[] {}, new int[] { 1 }, new int[] { 1 }, new int[] {}, new int[] { 1, 1 }, new int[] { 1 }, new int[] { 1 }, new int[] { 0 }, new int[] { -1 }, new int[] { 1 })
				.entrySet();
	}

	@Test
	void split() {
		final var day = newAbstractDay();
		final var expectedYear = 2022;
		final var expectedMonth = 10;
		final var expectedDay = 21;
		final var value = Integer.parseInt(String.format("%d%2d%2d", expectedYear, expectedMonth, expectedDay).replace(' ', '0'));

		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, value);

		final var values = day.split(4, 2);
		assertEquals(3, values.length);
		assertEquals(expectedYear, values[0]);
		assertEquals(expectedMonth, values[1]);
		assertEquals(expectedDay, values[2]);

	}

	private AbstractDay<Integer> newAbstractDay() {
		return newAbstractDay(new int[] { 0 }, new int[] { 1 });
	}

	@Test
	void splitInvalidArgs() {
		final var day = newAbstractDay();

		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, Integer.MAX_VALUE);

		assertThrows(IllegalArgumentException.class, () -> day.split());
		assertThrows(IllegalArgumentException.class, () -> day.split(new int[] {}));
		assertThrows(IllegalArgumentException.class, () -> day.split(-1));
	}

	@Test
	void splitExpTooLarge() {
		final var value = 2512;
		final var digits = 4;
		final var day = newAbstractDay(new int[] { value }, new int[] { digits });

		final var values = day.split(digits);

		assertEquals(1, values.length);
		assertEquals(value, values[0]);
	}

	@Test
	void splitValueNegative() {
		final var day = newAbstractDay();

		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, Integer.MIN_VALUE);

		assertThrows(IllegalArgumentException.class, () -> day.split(1));
	}

}
