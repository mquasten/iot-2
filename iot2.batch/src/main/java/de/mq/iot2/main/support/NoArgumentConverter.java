package de.mq.iot2.main.support;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

public class NoArgumentConverter implements Converter<List<String>, Object[]> {

	@Override
	public Object[] convert(final List<String> argList) {
	
		Assert.isTrue(argList.isEmpty(), "No Arguments expected.");
		return new Object[] {};
	}

}
