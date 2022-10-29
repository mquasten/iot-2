package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

class DayOfWeekDayImplTest {

	private static final String DESCRIPTION = "Beschreibung";

	private static final String ID_FIELD = "id";
	private static final String VALUE_FIELD = "value";

	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);

	@ParameterizedTest
	@EnumSource(DayOfWeek.class)
	void create(final DayOfWeek dayOfWeek) {
		final var day = new DayOfWeekDayImpl(dayGroup, dayOfWeek, DESCRIPTION);

		assertTrue(day.description().isPresent());
		assertEquals(DESCRIPTION, day.description().get());
		assertEquals(dayGroup, day.dayGroup());
		assertEquals(dayOfWeek.getValue(), ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(IdUtil.string2Long(DayOfWeekDayImpl.ENTITY_NAME), dayOfWeek.getValue()).toString(), ReflectionTestUtils.getField(day, ID_FIELD));
	}

	@Test
	void createWithoutDescription() {
		final var dayOfWeek = DayOfWeek.TUESDAY;
		final var day = new DayOfWeekDayImpl(dayGroup, dayOfWeek);

		assertFalse(day.description().isPresent());
		assertEquals(dayGroup, day.dayGroup());
		assertEquals(dayOfWeek.getValue(), ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(IdUtil.string2Long(DayOfWeekDayImpl.ENTITY_NAME), dayOfWeek.getValue()).toString(), ReflectionTestUtils.getField(day, ID_FIELD));
	}

	@ParameterizedTest
	@EnumSource(DayOfWeek.class)
	void value(final DayOfWeek dayOfWeek) {
		final var day = BeanUtils.instantiateClass(DayOfWeekDayImpl.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD, dayOfWeek.getValue());

		assertEquals(dayOfWeek, day.value());
	}

	@Test
	void valueInvalid() {
		final var day = BeanUtils.instantiateClass(DayOfWeekDayImpl.class);
		ReflectionTestUtils.setField(day, VALUE_FIELD, 10);
		assertThrows(IllegalArgumentException.class, () -> day.value());
	}

	@Test
	void matches() {
		final var day = new DayOfWeekDayImpl(dayGroup, DayOfWeek.TUESDAY);
		assertTrue(day.matches(LocalDate.of(2022, 10, 25)));
		assertFalse(day.matches(LocalDate.of(2022, 10, 26)));
	}

	@Test
	void matchesDateNull() {
		final var day = new DayOfWeekDayImpl(dayGroup, DayOfWeek.TUESDAY);

		assertThrows(IllegalArgumentException.class, () -> day.matches(null));
	}

}
