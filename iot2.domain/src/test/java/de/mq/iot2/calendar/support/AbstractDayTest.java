package de.mq.iot2.calendar.support;

import static de.mq.iot2.calendar.support.AbstractDay.SIGNUM_POSITIV_INT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.time.MonthDay;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.DayGroup;

class AbstractDayTest {

	private static final String VALUE_FIELD_NAME = "value";
	private static final String DESCRIPTION = "description";
	private static final int TYPE = 4711;

	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);

	AbstractDay<Integer> newAbstractDay(final DayGroup dayGroup, final int[] values, final int[] digits) {
		return newAbstractDay(dayGroup, values, digits, SIGNUM_POSITIV_INT);
	}

	@SuppressWarnings("unchecked")
	AbstractDay<Integer> newAbstractDay(final DayGroup dayGroup, final int[] values, final int[] digits,
			final int signum) {
		try {
			return BeanUtils.instantiateClass(
					((AbstractDay<Integer>) Mockito.mock(AbstractDay.class)).getClass().getDeclaredConstructor(
							DayGroup.class, int[].class, int[].class, int.class, int.class, String.class),
					dayGroup, values, digits, signum, TYPE, DESCRIPTION);
		} catch (final Exception exception) {
			throw runtimeExceptionn(exception);
		}
	}

	AbstractDay<Integer> newAbstractDay(int[] values, int[] digits) {
		return newAbstractDay(dayGroup, values, digits);
	}

	private RuntimeException runtimeExceptionn(final Exception exception) {
		if (exception.getCause() instanceof RuntimeException) {
			return (RuntimeException) exception.getCause();
		}
		return new IllegalStateException("Unable to create exception AbstractDay", exception);
	}

	@Test
	void defaultConstructon() throws NoSuchMethodException, SecurityException {

		assertTrue(BeanUtils.instantiateClass(
				Mockito.mock(AbstractDay.class).getClass().getDeclaredConstructor()) instanceof AbstractDay<?>);

	}

	@Test
	void create() {
		final var day = newAbstractDay(new int[] { 1968, 5, 28 }, new int[] { 4, 2, 2 });
		assertTrue(day.description().isPresent());
		assertEquals(Optional.of(DESCRIPTION), day.description());
		final var expectedValue = 19680528;
		assertEquals(expectedValue, ReflectionTestUtils.getField(day, VALUE_FIELD_NAME));
		assertEquals(new UUID(TYPE, expectedValue).toString(), ReflectionTestUtils.getField(day, "id"));
		assertEquals(dayGroup, day.dayGroup());
	}

	@Test
	void createNegative() {
		final var signum = -1;
		final var day = newAbstractDay(dayGroup, new int[] { 1968, 5, 28 }, new int[] { 4, 2, 2 }, signum);
		assertTrue(day.description().isPresent());
		assertEquals(Optional.of(DESCRIPTION), day.description());
		final var expectedValue = 19680528;
		assertEquals(signum * expectedValue, ReflectionTestUtils.getField(day, VALUE_FIELD_NAME));
		assertEquals(new UUID(TYPE, signum * expectedValue).toString(), ReflectionTestUtils.getField(day, "id"));
		assertEquals(dayGroup, day.dayGroup());
	}

	@Test
	void createMissingDayGroup() {
		assertThrows(IllegalArgumentException.class,
				() -> newAbstractDay(null, new int[] { 1968, 5, 28 }, new int[] { 4, 2, 2 }));
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

	@Test
	void createNegativeValues() {
		assertThrows(IllegalArgumentException.class, () -> newAbstractDay(new int[] { -1 }, new int[] { 1 }));
	}

	private static Collection<Entry<int[], int[]>> invalidConstructorArgs() {
		return Map.of(new int[] {}, new int[] { 1 }, new int[] { 1 }, new int[] {}, new int[] { 1, 1 }, new int[] { 1 },
				new int[] { 1 }, new int[] { 0 }).entrySet();
	}

	@Test
	void split() {
		final var day = newAbstractDay();
		final var expectedYear = 2022;
		final var expectedMonth = 10;
		final var expectedDay = 21;
		final var value = Integer
				.parseInt(String.format("%d%2d%2d", expectedYear, expectedMonth, expectedDay).replace(' ', '0'));

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

	@ParameterizedTest
	@ValueSource(ints = { -1, 1, -123, 123 })
	void signum(final int signum) {
		assertEquals(BigInteger.valueOf(signum).signum(),
				newAbstractDay(dayGroup, new int[] { 4711 }, new int[] { 1 }, signum).signum());
	}

	@Test
	void hash() {
		// Achtung das final muss for der Methode stehen, sonst wird hashcode nicht
		// aufgerufen, sch... Mockito.
		// Überschreiben können soll man die Methode aber hier auch nicht.
		final var day = newAbstractDayWeihnachten();
		assertEquals(day.getClass().hashCode() + Integer.valueOf(1225).hashCode(), day.hashCode());

		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, null);
		assertEquals(System.identityHashCode(day), day.hashCode());
	}

	private AbstractDay<Integer> newAbstractDayWeihnachten() {
		return newAbstractDay(new int[] { 12, 25 }, new int[] { 2, 2 });
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void equals() {
		// Achtung das final muss for der Methode stehen, sonst wird equals nicht
		// aufgerufen, sch... Mockito
		// Überschreiben können soll man die Methode aber hier auch nicht.
		assertTrue(newAbstractDayWeihnachten().equals(newAbstractDayWeihnachten()));
		assertFalse(newAbstractDayWeihnachten().equals(newAbstractDay()));
		assertFalse(newAbstractDayWeihnachten().equals(DESCRIPTION));
		assertFalse(newAbstractDayWeihnachten().equals(new DayOfMonthImpl(dayGroup, MonthDay.of(12, 25))));

		final var day = newAbstractDay();
		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, null);
		final var otherDay = newAbstractDay();
		assertFalse(day.equals(otherDay));
		assertFalse(otherDay.equals(day));
		assertTrue(day.equals(day));
		assertTrue(otherDay.equals(otherDay));

	}

}
