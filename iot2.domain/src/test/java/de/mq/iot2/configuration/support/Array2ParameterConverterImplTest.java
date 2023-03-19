package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.util.Pair;
import org.springframework.util.FileCopyUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleImpl;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;

class Array2ParameterConverterImplTest {
	private final Converter<Pair<String[], Pair<Map<String, Configuration>, Map<String, Cycle>>>, Parameter> converter = new Array2ParameterConverterImpl(
			new DefaultConversionService());

	@Test
	void globalParameterFull() throws IOException {
		final String[] columns = getLineFromFile(0);

		final Parameter parameter = converter.convert(Pair.of(columns, Pair.of(Map.of(), Map.of())));

		assertEquals(Key.DaysBack, parameter.key());
		assertEquals(columns[2], parameter.value());
		assertEquals(IdUtil.id(Long.valueOf(columns[3])), IdUtil.getId(parameter.configuration()));
		assertEquals(RuleKey.CleanUp, parameter.configuration().key());
		assertEquals(columns[5], parameter.configuration().name());
		assertTrue(parameter instanceof ParameterImpl);
	}

	@Test
	void globalParameterExists() throws IOException {
		final String[] columns = getLineFromFile(5);
		final Configuration configuration = BeanUtils.instantiateClass(ConfigurationImpl.class);
		IdUtil.assignId(configuration, IdUtil.id(1L));

		final Parameter parameter = converter.convert(Pair.of(columns, Pair.of(Map.of(IdUtil.getId(configuration), configuration), Map.of())));

		assertEquals(Key.UpTime, parameter.key());
		assertEquals(columns[2], parameter.value());
		assertEquals(IdUtil.id(Long.valueOf(columns[3])), IdUtil.getId(parameter.configuration()));
		assertEquals(configuration, parameter.configuration());
		assertTrue(parameter instanceof ParameterImpl);
	}

	@Test
	void cycleParameter() throws IOException {
		final String[] columns = getLineFromFile(2);
		final Configuration configuration = BeanUtils.instantiateClass(ConfigurationImpl.class);
		IdUtil.assignId(configuration, IdUtil.id(1L));
		final Cycle cycle = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycle, IdUtil.id(1L));

		final Parameter parameter = converter.convert(Pair.of(columns, Pair.of(Map.of(IdUtil.getId(configuration), configuration), Map.of(IdUtil.getId(cycle), cycle))));

		assertEquals(Key.UpTime, parameter.key());
		assertEquals(columns[2], parameter.value());
		assertEquals(IdUtil.id(Long.valueOf(columns[3])), IdUtil.getId(parameter.configuration()));
		assertEquals(configuration, parameter.configuration());
		assertTrue(parameter instanceof CycleParameterImpl);

		assertEquals(cycle, ((CycleParameterImpl) parameter).cycle());
	}

	@Test
	void wrongNumberOfColumns() throws IOException {
		assertEquals(Array2ParameterConverterImpl.WRONG_NUMBER_OF_COLUMNS_MESSAGE,
				assertThrows(IllegalArgumentException.class, () -> converter.convert(Pair.of(new String[] { ";;" }, Pair.of(Map.of(), Map.of())))).getMessage());
	}

	private String[] getLineFromFile(int lineNumber) throws IOException {
		try (final InputStream is = getClass().getClassLoader().getResourceAsStream("configuration.csv")) {
			final List<String[]> data = List.of(new String(FileCopyUtils.copyToByteArray(is)).split("\n", -1)).stream().map(line -> line.split("[;]", -1))
					.collect(Collectors.toList());
			return data.get(lineNumber);
		}
	}

}
