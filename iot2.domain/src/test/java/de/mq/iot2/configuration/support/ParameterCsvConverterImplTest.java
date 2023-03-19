package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleImpl;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;

class ParameterCsvConverterImplTest {

	private final long configurationId = RandomTestUtil.randomLong();
	long cycleId = RandomTestUtil.randomLong();
	private static final String CSV_DELIMITER = ";";
	private final Converter<Pair<Parameter, Boolean>, String[]> converter = new ParameterCsvConverterImpl(CSV_DELIMITER);
	private final Configuration configurationEndOfDay = new ConfigurationImpl(configurationId, RuleKey.EndOfDay, "EndofDayBatch");

	@Test
	final void convertFull() {
		final Parameter parameter = newCycleParameter();

		final String[] results = converter.convert(Pair.of(parameter, false));

		assertEquals(7, results.length);
		assertEquals(CycleParameterImpl.ENTITY_NAME, results[0]);
		assertEquals(parameter.key().name(), results[1]);
		assertEquals(parameter.value(), results[2]);
		assertEquals("" + configurationId, results[3]);
		assertEquals(parameter.configuration().key().name(), results[4]);
		assertEquals(parameter.configuration().name(), results[5]);
		assertEquals("" + cycleId, results[6]);
	}

	@Test
	final void convertWithoutConfiguration() {
		final Parameter parameter = newCycleParameter();

		final String[] results = converter.convert(Pair.of(parameter, true));

		assertEquals(7, results.length);
		assertEquals(CycleParameterImpl.ENTITY_NAME, results[0]);
		assertEquals(parameter.key().name(), results[1]);
		assertEquals(parameter.value(), results[2]);
		assertEquals("" + configurationId, results[3]);
		assertTrue(results[4].isEmpty());
		assertTrue(results[5].isEmpty());
		assertEquals("" + cycleId, results[6]);
	}

	@Test
	final void convertGlobal() {
		final Parameter parameter = new ParameterImpl(configurationEndOfDay, Key.UpTime, "07:00");

		final String[] results = converter.convert(Pair.of(parameter, true));

		assertEquals(7, results.length);
		assertEquals(ParameterImpl.ENTITY_NAME, results[0]);
		assertEquals(parameter.key().name(), results[1]);
		assertEquals(parameter.value(), results[2]);
		assertEquals("" + configurationId, results[3]);
		assertTrue(results[4].isEmpty());
		assertTrue(results[5].isEmpty());
		assertTrue(results[6].isEmpty());
	}

	private Parameter newCycleParameter() {
		final Cycle cycle = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycle, IdUtil.id(cycleId));
		return new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "07:00", cycle);

	}

}
