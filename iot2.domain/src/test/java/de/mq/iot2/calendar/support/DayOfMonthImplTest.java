package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

class DayOfMonthImplTest {

	private static final String ID_FIELD = "id";
	private static final String VALUE_FIELD = "value";
	private static final String DESCRIPTION = "Weihnachten";

	private DayGroup dayGroup = Mockito.mock(DayGroup.class);

	@ParameterizedTest
	@MethodSource("monthDays")
	void createEntity(MonthDay monthDay) {

		final var day = new DayOfMonthImpl(dayGroup, monthDay, DESCRIPTION);

		assertEquals(monthDay, day.value());
		assertTrue(day.description().isPresent());
		assertEquals(DESCRIPTION, day.description().get());
		final var expectedValue = dayMonthToValue(monthDay);
		assertEquals(expectedValue, ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(DayOfMonthImpl.ENTITY_NAME.hashCode(), expectedValue).toString(),
				ReflectionTestUtils.getField(day, ID_FIELD));
	}

	@ParameterizedTest
	@MethodSource("monthDays")
	void createEntityWithoutDescription(MonthDay monthDay) {
		final var day = new DayOfMonthImpl(dayGroup, monthDay);

		assertEquals(monthDay, day.value());
		assertFalse(day.description().isPresent());
		final var expectedValue = dayMonthToValue(monthDay);
		assertEquals(expectedValue, ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(DayOfMonthImpl.ENTITY_NAME.hashCode(), expectedValue).toString(),
				ReflectionTestUtils.getField(day, ID_FIELD));
	}

	private int dayMonthToValue(MonthDay monthDay) {
		return monthDay.getMonthValue() * 100 + monthDay.getDayOfMonth();
	}

	@Test
	void createEntityWithoutMonthDay() {
		assertThrows(IllegalArgumentException.class, () -> new DayOfMonthImpl(dayGroup, null));
	}

	@Test
	void value() {
		final var day = BeanUtils.instantiateClass(DayOfMonthImpl.class);
		final var expectedMonthDay = MonthDay.of(12, 25);

		ReflectionTestUtils.setField(day, VALUE_FIELD, dayMonthToValue(expectedMonthDay));

		assertEquals(expectedMonthDay, day.value());
	}

	@ParameterizedTest
	@MethodSource("invalidValues")
	void valueInvalid(final Integer value) {
		final var day = BeanUtils.instantiateClass(DayOfMonthImpl.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD, value);
		assertThrows(IllegalArgumentException.class, () -> day.value());
	}

	private static Collection<Integer> invalidValues() {
		return Arrays.asList((Integer) null, 10);
	}

	private static Collection<MonthDay> monthDays() {
		return Arrays.asList(MonthDay.of(1, 1), MonthDay.of(1, 31), MonthDay.of(12, 1), MonthDay.of(10, 21));
	}

	@Test
	void matches() {
		var monthDay = MonthDay.of(10, 23);
		final var day = new DayOfMonthImpl(dayGroup, monthDay);

		assertTrue(
				day.matches(LocalDate.of(Year.now().getValue(), monthDay.getMonthValue(), monthDay.getDayOfMonth())));
		assertFalse(day
				.matches(LocalDate.of(Year.now().getValue(), monthDay.getMonthValue() + 1, monthDay.getDayOfMonth())));
		assertFalse(day
				.matches(LocalDate.of(Year.now().getValue(), monthDay.getMonthValue(), monthDay.getDayOfMonth() + 1)));
	}

	@Test
	void constructorForPersistence() {
		assertTrue(BeanUtils.instantiateClass(DayOfMonthImpl.class) instanceof Day);
	}
}
