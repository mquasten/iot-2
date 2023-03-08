package de.mq.iot2.configuration.support;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.CsvUtil;
import jakarta.persistence.DiscriminatorValue;

@Component
class ParameterCsvConverterImpl implements Converter<Pair<Parameter, Boolean>, String[]> {

	private final String csvDelimiter;

	ParameterCsvConverterImpl(@Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
	}

	@Override
	public String[] convert(final Pair<Parameter, Boolean> pair) {
		Assert.notNull(pair, "Value is required.");
		final var parameter = pair.getFirst();
		final var configurationProcessed = pair.getSecond();

		final Stream<String> results = Stream.concat(Stream.concat(
				Stream.of(parameter.getClass().getAnnotation(DiscriminatorValue.class).value(), parameter.key().name(), CsvUtil.quote(parameter.value(), csvDelimiter)),
				configuration(parameter.configuration(), configurationProcessed)), cycle(parameter));

		return results.toArray(size -> new String[size]);
	}

	private Stream<String> configuration(final Configuration configuration, boolean processed) {
		if (processed) {
			return Stream.concat(Stream.of(CsvUtil.id(configuration)), CsvUtil.emptyColumns(2));
		}
		return Stream.of(CsvUtil.id(configuration), configuration.key().name(), CsvUtil.quote(configuration.name(), csvDelimiter));
	}

	private Stream<String> cycle(final Parameter parameter) {
		if (parameter instanceof CycleParameterImpl) {
			return Stream.of(CsvUtil.id(((CycleParameterImpl) parameter).cycle()));
		}
		return CsvUtil.emptyColumns(1);
	}
}
