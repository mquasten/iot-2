package de.mq.iot2.configuration.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.configuration.Parameter;

@Component
class ParameterCsvConverterImpl implements Converter<Pair<Parameter, Boolean>, String[]> {
	
	private final String csvDelimiter ;
	ParameterCsvConverterImpl(@Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.csvDelimiter=csvDelimiter;
	}

	@Override
	public String[] convert(final Pair<Parameter, Boolean> pair) {
		Assert.notNull(pair, "Value is required.");
		final var parameter = pair.getFirst();
		Assert.notNull(parameter, "Parameter is required.");
		final var configurationProcessed = pair.getSecond() != null ? pair.getSecond() : false;
		System.out.println(configurationProcessed);
		System.out.println(csvDelimiter);

		return null;
	}

}
