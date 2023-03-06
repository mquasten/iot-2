package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.RandomTestUtil;

class DayCsvConverterImplTest {

	private static final long CYCLE_ID = RandomTestUtil.randomLong();
	private static final long DAY_GROUP_ID = RandomTestUtil.randomLong();
	private static final String CSV_DELIMITER = ";";
	private final Converter<Pair<Day<?>, boolean[]>, String[]> converter = new DayCsvConverterImpl(CSV_DELIMITER);

	@Test
	void convertGaussDayFull() {
		final Day<?> day = newGausDay(newDayGroup());

		final String[] results = converter.convert(Pair.of(day, new boolean[] { false, false }));

		assertEquals(10, results.length);
		assertEquals(GaussDayImpl.ENTITY_NAME, results[0]);
		assertEquals("-2", results[1]);
		assertEquals(day.description().orElseThrow(), results[2]);
		assertEquals("" + DAY_GROUP_ID, results[3]);

		assertEquals(day.dayGroup().name(), results[4]);
		assertEquals("" + true, results[5]);
		assertEquals("" + CYCLE_ID, results[6]);
		assertEquals(day.dayGroup().cycle().name(), results[7]);
		assertEquals("" + day.dayGroup().cycle().priority(), results[8]);
		assertEquals("" + false, results[9]);

	}

	@Test
	void convertGaussDayProcessed() {
		final Day<?> day = newGausDay(newDayGroup());
		final String[] results = converter.convert(Pair.of(day, new boolean[] { true, true }));

		assertEquals(10, results.length);

		assertEquals(10, results.length);
		assertEquals(GaussDayImpl.ENTITY_NAME, results[0]);
		assertEquals("-2", results[1]);
		assertEquals(day.description().orElseThrow(), results[2]);
		assertEquals("" + DAY_GROUP_ID, results[3]);

		assertTrue(results[4].isEmpty());
		assertTrue(results[5].isEmpty());
		assertEquals("" + CYCLE_ID, results[6]);
		assertTrue(results[7].isEmpty());
		assertTrue(results[8].isEmpty());
		assertTrue(results[9].isEmpty());
	}

	@Test
	void convertWrongBooleanArray() {
		assertEquals(DayCsvConverterImpl.BOOLEAN_ARRAY_WRONG_SIZE,
				assertThrows(IllegalArgumentException.class, () -> converter.convert(Pair.of(newGausDay(newDayGroup()), new boolean[] { true }))).getMessage());
	}

	@Test
	void convertDayOfMonth() {
		final Day<?> day = new DayOfMonthImpl(newDayGroup(), MonthDay.of(5, 1), "Tag der Arbeit");

		final String[] results = converter.convert(Pair.of(day, new boolean[] { true, true }));

		assertEquals(10, results.length);
		assertEquals(DayOfMonthImpl.ENTITY_NAME, results[0]);
		assertEquals("01.05", results[1]);
		assertEquals(day.description().orElseThrow(), results[2]);
		assertEquals("" + DAY_GROUP_ID, results[3]);
		assertTrue(results[4].isEmpty());
		assertTrue(results[5].isEmpty());
		assertEquals("" + CYCLE_ID, results[6]);
		assertTrue(results[7].isEmpty());
		assertTrue(results[8].isEmpty());
		assertTrue(results[9].isEmpty());

	}

	@Test
	void convertDayOfWeekDayImpl() {
		final Day<?> day = new DayOfWeekDayImpl(newDayGroup(), DayOfWeek.SUNDAY);
		final String[] results = converter.convert(Pair.of(day, new boolean[] { true, true }));

		assertEquals(10, results.length);
		assertEquals(DayOfWeekDayImpl.ENTITY_NAME, results[0]);
		assertEquals("" + DayOfWeek.SUNDAY.getValue(), results[1]);
		assertTrue(results[2].isEmpty());
		assertEquals("" + DAY_GROUP_ID, results[3]);
		assertTrue(results[4].isEmpty());
		assertTrue(results[5].isEmpty());
		assertEquals("" + CYCLE_ID, results[6]);
		assertTrue(results[7].isEmpty());
		assertTrue(results[8].isEmpty());
		assertTrue(results[9].isEmpty());
	}

	@Test
	void convertLocalDateDay() {
		final Day<?> day = new LocalDateDayImp(newDayGroup(), LocalDate.of(1831, 6, 13));
		final String[] results = converter.convert(Pair.of(day, new boolean[] { true, true }));

		assertEquals(10, results.length);
		assertEquals(LocalDateDayImp.ENTITY_NAME, results[0]);
		assertEquals("13.06.1831", results[1]);
		assertTrue(results[2].isEmpty());
		assertEquals("" + DAY_GROUP_ID, results[3]);
		assertTrue(results[4].isEmpty());
		assertTrue(results[5].isEmpty());
		assertEquals("" + CYCLE_ID, results[6]);
		assertTrue(results[7].isEmpty());
		assertTrue(results[8].isEmpty());
		assertTrue(results[9].isEmpty());
	}

	private DayGroup newDayGroup() {
		final Cycle cycle = new CycleImpl(CYCLE_ID, "Freizeit", 101, false);
		final DayGroup dayGroup = new DayGroupImpl(cycle, DAY_GROUP_ID, "Feiertage", true);
		return dayGroup;
	}

	private Day<?> newGausDay(final DayGroup dayGroup) {
		final Day<?> day = new GaussDayImpl(dayGroup, -2, "Karfreitag");
		return day;
	}

}
