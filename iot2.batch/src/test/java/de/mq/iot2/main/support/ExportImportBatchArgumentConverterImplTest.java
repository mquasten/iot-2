package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.EmptyResultDataAccessException;

public class ExportImportBatchArgumentConverterImplTest {
	
	private final Converter<List<String>, Object[]> converter = new ExportImportBatchArgumentConverterImpl();
	
	@Test
	void convert() {
		final var name = "Calendar.csv";
		final Object[] results = converter.convert(List.of(name));
		assertEquals(1, results.length);
		assertTrue(results[0] instanceof File); 
		assertEquals(name, ((File)results[0]).getName());
	}
	
	@Test
	void convertWrongNumberOfArguments() {
		assertThrows(EmptyResultDataAccessException.class,() -> converter.convert(List.of()));
	}

}
