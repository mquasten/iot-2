package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class EndOfDayBatchArgumentConverterImplTest {
	private final Converter<List<String>, Object[]> converter = new EndOfDayBatchArgumentConverterImpl();

	@Test
	void convert() {
		final var result = converter.convert(Collections.emptyList());
		assertEquals(1, result.length);
		assertEquals(LocalDate.now().plusDays(1), result[0]);
	}

	@Test
	void convertWithDate() {
		final var result = converter.convert(Collections.singletonList("13.06.1831"));
		assertEquals(1, result.length);
		assertEquals(LocalDate.of(1831, 6, 13), result[0]);
	}

	@Test
	void convertInvalidNumberOfArguments() {
		assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of("arg1", "arg2")));
	}

}
