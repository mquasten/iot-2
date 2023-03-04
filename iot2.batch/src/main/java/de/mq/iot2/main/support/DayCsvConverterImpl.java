package de.mq.iot2.main.support;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.Day;

@Component
class DayCsvConverterImpl implements Converter<Day<?>, String> {

	@Override
	public String convert(final Day<?> source) {
		// TODO Auto-generated method stub
		return null;
	}

}
