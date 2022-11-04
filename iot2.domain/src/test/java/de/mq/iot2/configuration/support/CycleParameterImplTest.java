package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.RandomTestUtil;

class CycleParameterImplTest {

	private static final String CYCLE_FIELD_NAME = "cycle";
	private final Configuration configuration = Mockito.mock(Configuration.class);
	private static final String VALUE = RandomTestUtil.randomString();
	private final Cycle cycle = Mockito.mock(Cycle.class);

	@Test
	void create() {
		final var key = Key.MinSunDownTime;
		final var cycleParameter = new CycleParameterImpl(configuration, key, VALUE, cycle);
		assertEquals(configuration, cycleParameter.configuration());
		assertEquals(key, cycleParameter.key());
		assertEquals(VALUE, cycleParameter.value());
		assertEquals(cycle, cycleParameter.cycle());
	}

	@Test
	void createWithOutCycle() {
		final var key = Key.MinSunDownTime;
		assertThrows(IllegalArgumentException.class, () -> new CycleParameterImpl(configuration, key, VALUE, null));
	}

	@Test
	void cycle() {
		final var cycleParameter = BeanUtils.instantiateClass(CycleParameterImpl.class);

		assertThrows(IllegalArgumentException.class, () -> cycleParameter.cycle());

		ReflectionTestUtils.setField(cycleParameter, CYCLE_FIELD_NAME, cycle);
		assertEquals(cycle, cycleParameter.cycle());
	}

}
