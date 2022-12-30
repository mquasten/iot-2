package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class EndOfDayUpdateBatchArgumentConverterImplTest {

	private final Converter<List<String>, Object[]> converter = new EndOfDayUpdateBatchArgumentConverterImpl();

	@Test
	void convert() {

		final Object[] results = converter.convert(List.of("11:11"));
		assertEquals(1, results.length);
		assertEquals(LocalTime.of(11, 11, 0), results[0]);
	}

	@Test
	void convertNoArg() {

		final Object[] results = converter.convert(List.of());
		assertEquals(1, results.length);
		assertTrue(((LocalTime) results[0]).until(LocalTime.now(), ChronoUnit.MILLIS) < 50);

	}
	
	@Test
	void convertWrongNumberOfArguments() {
		assertEquals(EndOfDayUpdateBatchArgumentConverterImpl.MESSAGE_ONE_OPTIONAL_ARGUMENT, assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of("11:11", "12:12"))).getMessage());
	}

}
