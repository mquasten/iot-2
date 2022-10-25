package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

class GaussDayImplTest {

	private static final String YEAR_SUPPLIER_FIELD = "yearSupplier";

	private static final int OFFSET_KARFREITAG = -2;

	private final DayGroup dayGroup = Mockito.mock(DayGroup.class);

	private static final String DESCRIPTION = "Karfreitag";

	private static final String ID_FIELD = "id";
	private static final String VALUE_FIELD = "value";

	@Test
	void create() {
		final var day = new GaussDayImpl(dayGroup, OFFSET_KARFREITAG, DESCRIPTION);

		assertTrue(day.description().isPresent());
		assertEquals(DESCRIPTION, day.description().get());
		assertEquals(dayGroup, day.dayGroup());
		assertEquals(OFFSET_KARFREITAG, ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(GaussDayImpl.ENTITY_NAME.hashCode(), OFFSET_KARFREITAG).toString(), ReflectionTestUtils.getField(day, ID_FIELD));
	}
	
	@Test
	void createInvaildValue() {
		assertThrows(IllegalArgumentException.class, ()-> new GaussDayImpl(dayGroup, 400, DESCRIPTION));
	}

	@Test
	void createWithoutDescription() {
		final var day = new GaussDayImpl(dayGroup, OFFSET_KARFREITAG);

		assertFalse(day.description().isPresent());
		assertEquals(dayGroup, day.dayGroup());
		assertEquals(OFFSET_KARFREITAG, ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(GaussDayImpl.ENTITY_NAME.hashCode(), OFFSET_KARFREITAG).toString(), ReflectionTestUtils.getField(day, ID_FIELD));
	}

	@ParameterizedTest
	@ValueSource(ints = { -300,  400, Integer.MAX_VALUE })
	void createInvalidOffset(final int offset) {
		assertThrows(IllegalArgumentException.class, () -> new GaussDayImpl(dayGroup, offset));
	}

	@ParameterizedTest
	@MethodSource("esterdatesWki")
	void value(final LocalDate esterdate) {
		final var day = new GaussDayImpl(dayGroup, 0);
		@SuppressWarnings("unchecked")
		final Supplier<Year> yearSupplier = Mockito.mock(Supplier.class);
		Mockito.when(yearSupplier.get()).thenReturn(Year.of(esterdate.getYear()));
		ReflectionTestUtils.setField(day, YEAR_SUPPLIER_FIELD, yearSupplier);

		assertEquals(esterdate, day.value());
	}

	static Collection<LocalDate> esterdatesWki() {
		return Arrays.asList(LocalDate.of(2017, Month.APRIL, 16), LocalDate.of(2018, Month.APRIL, 1), LocalDate.of(2019, Month.APRIL, 21), LocalDate.of(2020, Month.APRIL, 12),
				LocalDate.of(2021, Month.APRIL, 4), LocalDate.of(2022, Month.APRIL, 17), LocalDate.of(2023, Month.APRIL, 9), LocalDate.of(2024, Month.MARCH, 31), LocalDate.of(2025, Month.APRIL, 20),
				LocalDate.of(2026, Month.APRIL, 5), LocalDate.of(2027, Month.MARCH, 28), LocalDate.of(2028, Month.APRIL, 16), LocalDate.of(2029, Month.APRIL, 1), LocalDate.of(2030, Month.APRIL, 21),
				LocalDate.of(2031, Month.APRIL, 13), LocalDate.of(2032, Month.MARCH, 28), LocalDate.of(2033, Month.APRIL, 17), LocalDate.of(2034, Month.APRIL, 9), LocalDate.of(2035, Month.MARCH, 25),
				LocalDate.of(2036, Month.APRIL, 13), LocalDate.of(2037, Month.APRIL, 5));
	}

	@ParameterizedTest
	@MethodSource("esterdatesWki")
	void matches(final LocalDate esterdate) {

		final var day = new GaussDayImpl(dayGroup, OFFSET_KARFREITAG);
		@SuppressWarnings("unchecked")
		final Supplier<Year> yearSupplier = Mockito.mock(Supplier.class);
		Mockito.when(yearSupplier.get()).thenReturn(Year.of(esterdate.getYear()));
		ReflectionTestUtils.setField(day, YEAR_SUPPLIER_FIELD, yearSupplier);

		assertTrue(day.matches(esterdate.plusDays(OFFSET_KARFREITAG)));
		assertFalse(day.matches(esterdate));
	}


	
	@Test
	void yearSupplier() {
		final var day = new GaussDayImpl(dayGroup, OFFSET_KARFREITAG);
		final var yearSupplier = (Supplier<?>) ReflectionTestUtils.getField(day, YEAR_SUPPLIER_FIELD);
		assertEquals(Year.now(), yearSupplier.get());
	}

	@Test
	void constructorForPersistence() {
		assertTrue(BeanUtils.instantiateClass(GaussDayImpl.class) instanceof Day);
	}
}
