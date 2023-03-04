package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

class DayGroupBatchConverterImpl implements Converter<List<String>, Object[]> {

	static final String DATE_NOT_IN_THE_FUTURE_MESSAGE = "FromDate should be in the future.";
	static final String MAX_DAYS_LIMIT_MESSAGE = "More than %s days.";
	static final String FROM_LESS_OR_EQUALS_TO_DATE_MESSAGE = "FromDate should be <= toDate.";
	static final String INVALID_NUMBER_OF_PARAMETERS_MESSAGE = "DayGroupBatchImpl has 2 mandatory and 1 optional Parameter.";
	static final int MAX_DAY_LIMIT = 30;

	private final Supplier<LocalDate> now;

	DayGroupBatchConverterImpl() {
		now = () -> LocalDate.now();
	}

	DayGroupBatchConverterImpl(Supplier<LocalDate> now) {
		this.now = now;
	}

	@Override
	public Object[] convert(final List<String> objects) {
		Assert.isTrue(objects.size() <= 3 && objects.size() >= 2, INVALID_NUMBER_OF_PARAMETERS_MESSAGE);
		final var fromDate = localDate(objects.get(1));
		final var toDate = objects.size() == 3 ? localDate(objects.get(2)) : localDate(objects.get(1));

		Assert.isTrue(fromDate.isAfter(now.get()), DATE_NOT_IN_THE_FUTURE_MESSAGE);
		Assert.isTrue(fromDate.isBefore(toDate) || fromDate.isEqual(toDate), FROM_LESS_OR_EQUALS_TO_DATE_MESSAGE);
		Assert.isTrue(fromDate.until(toDate, ChronoUnit.DAYS) + 1 <= MAX_DAY_LIMIT, String.format(MAX_DAYS_LIMIT_MESSAGE, MAX_DAY_LIMIT));
		return new Object[] { objects.get(0), fromDate, toDate };

	}

	private LocalDate localDate(final String dateString) {
		return LocalDate.parse(dateString, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN));
	}

}
