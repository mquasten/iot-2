package de.mq.iot2.configuration.support;

import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;

@Component
class Array2ParameterConverterImpl implements Converter<Pair<String[], Pair<Map<String, Configuration>, Map<String, Cycle>>>, Parameter> {
	static final String WRONG_NUMBER_OF_COLUMNS_MESSAGE = "7 columns expected.";

	private Map<String, BiFunction<String[], Pair<Configuration, Map<String, Cycle>>, Parameter>> pararemtes = Map.of(ParameterImpl.ENTITY_NAME, this::globalParameter,
			CycleParameterImpl.ENTITY_NAME, this::cycleParameter);
	private final ConversionService conversionService;

	public Array2ParameterConverterImpl(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	private Parameter globalParameter(final String[] columns, final Pair<Configuration, Map<String, Cycle>> pair) {

		final var key = value(columns, 1, Key.class);
		final var value = value(columns, 2);
		return new ParameterImpl(pair.getFirst(), key, value);

	}

	private Parameter cycleParameter(final String[] columns, final Pair<Configuration, Map<String, Cycle>> pair) {
		final var key = value(columns, 1, Key.class);
		final var value = value(columns, 2);
		final Map<String, Cycle> cycles = pair.getSecond();
		final var cycleId = IdUtil.id(value(columns, 6, Long.class));
		final Cycle cycle = cycles.get(cycleId);
		Assert.notNull(cycle, "Cycle required.");
		return new CycleParameterImpl(pair.getFirst(), key, value, cycle);
	}

	@Override
	public Parameter convert(Pair<String[], Pair<Map<String, Configuration>, Map<String, Cycle>>> data) {
		Assert.notNull(data, "Input is required.");
		final String[] columns = data.getFirst();
		final Map<String, Configuration> configurations = data.getSecond().getFirst();
		final Map<String, Cycle> cycles = data.getSecond().getSecond();

		Assert.isTrue(columns.length == 7, WRONG_NUMBER_OF_COLUMNS_MESSAGE);

		final Configuration configuration = configuration(columns, configurations);

		final var type = value(columns, 0);

		final BiFunction<String[], Pair<Configuration, Map<String, Cycle>>, Parameter> function = pararemtes.get(type);
		Assert.notNull(function, String.format("ParameterType %s unkown.", type));
		return function.apply(columns, Pair.of(configuration, cycles));
	}

	private Configuration configuration(final String[] columns, final Map<String, Configuration> configurations) {
		final var configurationId = value(columns, 3, Long.class);

		if (configurations.containsKey(IdUtil.id(configurationId))) {
			return configurations.get(IdUtil.id(configurationId));
		}

		final var ruleKey = value(columns, 4, RuleKey.class);
		final var name = value(columns, 5);
		return new ConfigurationImpl(configurationId, ruleKey, name);
	}

	private String value(final String[] columns, final int col) {
		final String result = columns[col].strip();
		Assert.hasText(result, String.format("Value reuired in Column %s", col));
		return result;
	}

	private <T> T value(final String[] columns, final int col, Class<T> type) {
		return value(value(columns, col), type);
	}

	private <T> T value(final String value, final Class<T> type) {
		return conversionService.convert(value, type);
	}

}
