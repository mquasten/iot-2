package de.mq.iot2.calendar.support;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Day;

@Component
class DayCsvConverterImpl implements Converter<Pair<Day<?>, boolean[]>, String[]> {

	@Override
	public String[] convert(Pair<Day<?>, boolean[]> pair) {
		Assert.notNull(pair, "Value is required.");
		Assert.isTrue(pair.getSecond().length==2, "2 flags required.");
		final var day = pair.getFirst();
		Assert.notNull(day, "Day is required.");
		Assert.notNull(pair.getSecond(), "Flags required.");
		final var dataGroupProcessed=pair.getSecond()[0];
		final var cycleProcessed=pair.getSecond()[1];
		System.out.println(dataGroupProcessed);
		System.out.println(cycleProcessed);
		
		return null;
	}

	

}
