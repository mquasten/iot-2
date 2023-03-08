package de.mq.iot2.calendar.support;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
@Component
class Array2DayConverterImpl implements  Converter<Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>>, Day<?>> {

	@Override
	public Day<?> convert(Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>> source) {
		// TODO Auto-generated method stub
		return null;
	}

}
