package de.mq.iot2.main.support;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;

class EndOfDayUpdateBatchArgumentConverterImpl implements Converter<List<String>, Object[]> {

	@Override
	public Object[] convert(List<String> objects) {
		
		Assert.isTrue(objects.size() <= 1, "EndOfDayBatchImpl has 1 optional Parameter.");
		final String value = DataAccessUtils.singleResult(objects);
		if (value == null) {
			return new Object[] { LocalTime.now()};
		}
		
		return new Object[] {LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"))};
		
	}

}
