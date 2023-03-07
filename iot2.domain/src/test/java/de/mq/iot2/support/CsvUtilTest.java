package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleImpl;

class CsvUtilTest {
	private static final String DELIMITER = ";";

	@Test
	void string() {
		final String text = RandomTestUtil.randomString();
		assertEquals(text, CsvUtil.string(Optional.of(text)));
	}

	@Test
	void stringEmpty() {
		assertTrue(CsvUtil.string(Optional.empty()).isEmpty());
	}

	@Test
	void emptyColumns() {
		final Collection<String> results = CsvUtil.emptyColumns(3).collect(Collectors.toList());
		assertEquals(3, results.size());
		results.stream().forEach(col -> assertTrue(col.isEmpty()));

	}

	@Test
	void id() {
		final long id = RandomTestUtil.randomLong();
		final Cycle cycle =  Mockito.mock(CycleImpl.class);
		IdUtil.assignId(cycle, IdUtil.id(id));
		assertEquals("" + id, CsvUtil.id(cycle));
	}
	
	@Test
	void idWrong() {
		final Cycle cycle = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycle, UUID.randomUUID().toString());
		assertEquals(CsvUtil.WRONG_ID_MESSAGE, assertThrows(IllegalArgumentException.class, () -> CsvUtil.id(cycle)).getMessage());
	}
	
	@Test
	void quote() {
		final var text =String.format("ein%sString", DELIMITER);
		assertEquals(String.format("\"%s\"", text), CsvUtil.quote(text, DELIMITER));
	}
	
	@Test
	void quoteWithoutDelimiter() {
		final var text = "einString";
		assertEquals(text, CsvUtil.quote(text, DELIMITER));
	}
	
}
