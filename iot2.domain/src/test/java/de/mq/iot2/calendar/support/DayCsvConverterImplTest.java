package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

class DayCsvConverterImplTest {

	private static final long DAY_GROUP_ID = 1L;
	private static final String CSV_DELIMITER = ";";
	private final Converter<Pair<Day<?>, boolean[]>, String[]> converter = new DayCsvConverterImpl(CSV_DELIMITER);

	@Test
	void convertFull() {
		final Cycle cycle = new CycleImpl("Freizeit", 101, false);
		final DayGroup dayGroup = new DayGroupImpl(cycle, DAY_GROUP_ID, "Feiertage", true);
		final Day<?> day = new GaussDayImpl(dayGroup, -2, "Karfreitag");

		final String[] results = converter.convert(Pair.of(day, new boolean[] { true, true }));

		assertEquals(4, results.length);
		assertEquals(GaussDayImpl.ENTITY_NAME, results[0]);
		assertEquals("-2", results[1]);
		assertEquals(day.description().orElseThrow(), results[2]);
		assertEquals("" + DAY_GROUP_ID, results[3]);

		System.out.println(StringUtils.arrayToDelimitedString(results, CSV_DELIMITER));
	}

}
