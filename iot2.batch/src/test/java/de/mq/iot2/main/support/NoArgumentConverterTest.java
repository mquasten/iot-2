package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class NoArgumentConverterTest {

	private final Converter<List<String>, Object[]> converter = new NoArgumentConverterImpl();

	@Test
	void convert() {
		final var result = converter.convert(Collections.emptyList());
		assertEquals(0, result.length);
		assertTrue(result instanceof Object[]);
	}

	@Test
	void convertInvalidWithArgs() {
		assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of("arg")));
	}

}
