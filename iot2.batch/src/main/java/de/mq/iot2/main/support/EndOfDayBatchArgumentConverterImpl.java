package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;

class EndOfDayBatchArgumentConverterImpl implements Converter<List<String>, Object[]> {

	@Override
	public Object[] convert(final List<String> objects) {

		Assert.isTrue(objects.size() <= 1, "EndOfDayBatchImpl has 1 optional Parameter.");
		final String value = DataAccessUtils.singleResult(objects);

		if (value == null) {
			return new Object[] { LocalDate.now().plusDays(1) };
		}

		return new Object[] { localDate(value) };
	}

	private LocalDate localDate(final String dateString) {
		return LocalDate.parse(dateString,
				DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN));
	}

}
