package de.mq.iot2.main.support;

import static de.mq.iot2.main.support.DayGroupBatchConverterImpl.DATE_NOT_IN_THE_FUTURE_MESSAGE;
import static de.mq.iot2.main.support.DayGroupBatchConverterImpl.FROM_LESS_OR_EQUALS_TO_DATE_MESSAGE;
import static de.mq.iot2.main.support.DayGroupBatchConverterImpl.INVALID_NUMBER_OF_PARAMETERS_MESSAGE;
import static de.mq.iot2.main.support.DayGroupBatchConverterImpl.MAX_DAYS_LIMIT_MESSAGE;
import static de.mq.iot2.main.support.DayGroupBatchConverterImpl.MAX_DAY_LIMIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.convert.converter.Converter;

class DayGroupBatchConverterImplTest {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static String GROUP = "Urlaub";
	private final Converter<List<String>, Object[]> converter = new DayGroupBatchConverterImpl(() -> LocalDate.MIN);
	private final static String FROM_DATE = "24.12.2022";
	private final static String TO_DATE = "01.01.2023";

	@Test
	void convert() {
		final Object[] results = converter.convert(List.of(GROUP, FROM_DATE, TO_DATE));

		assertEquals(GROUP, results[0]);
		assertEquals(toDate(FROM_DATE), results[1]);
		assertEquals(toDate(TO_DATE), results[2]);
	}

	private LocalDate toDate(final String fromDate) {
		return LocalDate.parse(fromDate, DATE_FORMATTER);
	}

	@Test
	void convertSingleDay() {
		final Object[] results = converter.convert(List.of(GROUP, FROM_DATE));

		assertEquals(GROUP, results[0]);
		assertEquals(toDate(FROM_DATE), results[1]);
		assertEquals(toDate(FROM_DATE), results[2]);
	}

	@ParameterizedTest
	@ValueSource(strings = { "UrLaub", "Urlaub,31.12.2022,31.12.2022,31.12.2022" })
	void convertInvalidMumberOfParameters(final String values) {
		assertEquals(INVALID_NUMBER_OF_PARAMETERS_MESSAGE, assertThrows(IllegalArgumentException.class, () -> converter.convert(Arrays.asList(values.split(",")))).getMessage());
	}

	@Test
	void convertFromAfterToDate() {
		assertEquals(FROM_LESS_OR_EQUALS_TO_DATE_MESSAGE, assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of(GROUP, TO_DATE, FROM_DATE))).getMessage());
	}

	@Test
	void convertDayLimit() {
		assertEquals(String.format(MAX_DAYS_LIMIT_MESSAGE, MAX_DAY_LIMIT), assertThrows(IllegalArgumentException.class, () -> new DayGroupBatchConverterImpl()
				.convert(List.of(GROUP, LocalDate.now().plusDays(1).format(DATE_FORMATTER), LocalDate.now().plusDays(31).format(DATE_FORMATTER)))).getMessage());
	}

	@Test
	void convertFromBeforeNow() {
		final var date = LocalDate.now();
		final var converter = new DayGroupBatchConverterImpl(() -> date);

		assertEquals(DATE_NOT_IN_THE_FUTURE_MESSAGE,
				assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of(GROUP, date.format(DATE_FORMATTER)))).getMessage());
	}

}
