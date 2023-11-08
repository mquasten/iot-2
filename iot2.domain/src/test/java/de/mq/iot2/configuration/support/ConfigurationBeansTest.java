package de.mq.iot2.configuration.support;

import static de.mq.iot2.configuration.support.ConfigurationBeans.EMPTY_OPTIONAL_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionException;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.support.RandomTestUtil;

class ConfigurationBeansTest {

	private static final LocalDate MAXWEELS_BIRTHDATE = LocalDate.of(1831, 6, 18);
	private ConfigurationBeans configurationBeans = new ConfigurationBeans();

	@Test
	void conversionServiceString2LocalTime() {
		assertEquals(LocalTime.of(11, 11), configurationBeans.conversionService().convert("11:11", LocalTime.class));
		assertEquals(LocalTime.of(1, 1), configurationBeans.conversionService().convert("01:01", LocalTime.class));
		assertEquals(LocalTime.of(1, 1), configurationBeans.conversionService().convert("1:1", LocalTime.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "11.11:11", "11", "x:11", "" })
	void conversionServiceInvalid(final String value) {
		assertThrows(ConversionException.class, () -> configurationBeans.conversionService().convert(value, LocalTime.class));

	}

	@Test
	void conversionServiceLocalTime2String() {
		assertEquals("11:11", configurationBeans.conversionService().convert(LocalTime.of(11, 11), String.class));
		assertEquals("01:01", configurationBeans.conversionService().convert(LocalTime.of(1, 1), String.class));
		assertEquals("23:59", configurationBeans.conversionService().convert(LocalTime.of(23, 59), String.class));
	}

	@Test
	void localDate2StringConverter() {
		assertEquals("18.06.1831", configurationBeans.conversionService().convert(MAXWEELS_BIRTHDATE, String.class));
	}

	@Test
	void cycle2StringConverter() {
		final var cycle = Mockito.mock(Cycle.class);
		final var cycleName = RandomTestUtil.randomString();

		when(cycle.name()).thenReturn(cycleName);

		assertEquals(cycleName, configurationBeans.conversionService().convert(cycle, String.class));
	}

	@Test
	void optional2StringConverter() {
		final var value = RandomTestUtil.randomString();
		assertEquals(value, configurationBeans.conversionService().convert(Optional.of(value), String.class));
		assertEquals("18.06.1831", configurationBeans.conversionService().convert(Optional.of(MAXWEELS_BIRTHDATE), String.class));
		assertEquals(EMPTY_OPTIONAL_STRING, configurationBeans.conversionService().convert(Optional.empty(), String.class));
	}

}
