package de.mq.iot2.configuration.support;

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
	
	private static final String CSV_DELIMITER = ";";
	private final Converter<Pair<Parameter, Boolean>, String[]> converter = new ParameterCsvConverterImpl(CSV_DELIMITER);
	
	@Test
	final void convert() {
		final Configuration configurationEndOfDay = new ConfigurationImpl(RandomTestUtil.randomLong(), RuleKey.EndOfDay, "EndofDayBatch");
		final Cycle cycle = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycle, IdUtil.id(1L));
		final Parameter parameter = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "07:00", cycle);
		
		converter.convert(Pair.of(parameter, false));
	}

}
