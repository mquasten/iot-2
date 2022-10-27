package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.DayGroup;

class LocalDateDayImpTest {

	private static final LocalDate DATE_NEGATIVE_YEAR = LocalDate.of(-1234, 1, 1);
	private static final String DESCRIPTION = "Maxwell's birthday";
	private static final String VALUE_FIELD_NAME = "value";
	private static final String ID_FIELD_NAME = "id";
	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);
	private final LocalDate MAXWELL_BIRTHDATE = LocalDate.of(1831, 6, 13);

	@Test
	void create() {

		final var day = new LocalDateDayImp(dayGroup, MAXWELL_BIRTHDATE, DESCRIPTION);

		assertEquals(dayGroup, day.dayGroup());
		assertEquals(Optional.of(DESCRIPTION), day.description());
		assertEquals(dateToInteger(MAXWELL_BIRTHDATE), ReflectionTestUtils.getField(day, VALUE_FIELD_NAME));
		assertEquals(1, day.signum());
		assertEquals(new UUID(LocalDateDayImp.ENTITY_NAME.hashCode(), dateToInteger(MAXWELL_BIRTHDATE)).toString(), ReflectionTestUtils.getField(day, ID_FIELD_NAME));
	}

	@Test
	void createYearNegative() {
		final var day = new LocalDateDayImp(dayGroup, DATE_NEGATIVE_YEAR, DESCRIPTION);

		assertEquals(dayGroup, day.dayGroup());
		assertEquals(Optional.of(DESCRIPTION), day.description());
		assertEquals(dateToInteger(DATE_NEGATIVE_YEAR), ReflectionTestUtils.getField(day, VALUE_FIELD_NAME));
		assertEquals(-1, day.signum());
		assertEquals(new UUID(LocalDateDayImp.ENTITY_NAME.hashCode(), dateToInteger(DATE_NEGATIVE_YEAR)).toString(), ReflectionTestUtils.getField(day, ID_FIELD_NAME));
	}

	@Test
	void createWithotDescription() {
		final var day = new LocalDateDayImp(dayGroup, MAXWELL_BIRTHDATE);

		assertEquals(dayGroup, day.dayGroup());
		assertEquals(Optional.empty(), day.description());
		assertEquals(dateToInteger(MAXWELL_BIRTHDATE), ReflectionTestUtils.getField(day, VALUE_FIELD_NAME));
		assertEquals(1, day.signum());
		assertEquals(new UUID(LocalDateDayImp.ENTITY_NAME.hashCode(), dateToInteger(MAXWELL_BIRTHDATE)).toString(), ReflectionTestUtils.getField(day, ID_FIELD_NAME));
	}

	@Test
	void createWithLocaleDateNull() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateDayImp(dayGroup, null));
	}

	private int dateToInteger(final LocalDate date) {
		return Integer.parseInt(String.format("%4d%2d%2d", date.getYear(), date.getMonthValue(), date.getDayOfMonth()).replace(' ', '0'));
	}

	@Test
	void value() {
		final var day = BeanUtils.instantiateClass(LocalDateDayImp.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, dateToInteger(MAXWELL_BIRTHDATE));

		assertEquals(MAXWELL_BIRTHDATE, day.value());
	}

	@Test
	void valueNeagive() {
		final var day = BeanUtils.instantiateClass(LocalDateDayImp.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, dateToInteger(DATE_NEGATIVE_YEAR));

		assertEquals(DATE_NEGATIVE_YEAR, day.value());
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 4711, -4711, Integer.MAX_VALUE, Integer.MIN_VALUE })
	void valueWrongValues() {
		final var day = BeanUtils.instantiateClass(LocalDateDayImp.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, 4711);

		assertThrows(IllegalArgumentException.class, () -> day.value());
	}

	@Test
	void matches() {
		final var day = BeanUtils.instantiateClass(LocalDateDayImp.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD_NAME, dateToInteger(MAXWELL_BIRTHDATE));

		assertTrue(day.matches(MAXWELL_BIRTHDATE));
		assertFalse(day.matches(DATE_NEGATIVE_YEAR));
	}

}
