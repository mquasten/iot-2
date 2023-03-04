package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

	@ParameterizedTest
	@EnumSource(Key.class)
	void create(final Key key) {
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

	@Test
	void hash() {
		final var key = Key.DaysBack;
		final var cycleParameter = new CycleParameterImpl(configuration, key, VALUE, cycle);
		assertEquals(CycleParameterImpl.class.hashCode() + key.hashCode() + configuration.hashCode() + cycle.hashCode(), cycleParameter.hashCode());

		ReflectionTestUtils.setField(cycleParameter, CYCLE_FIELD_NAME, null);

		assertEquals(CycleParameterImpl.class.hashCode() + key.hashCode() + configuration.hashCode(), cycleParameter.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void equals() {
		final var key = Key.MaxSunUpTime;
		final var cycleParameter = new CycleParameterImpl(configuration, key, VALUE, cycle);

		assertFalse(cycleParameter.equals(VALUE));

		assertTrue(cycleParameter.equals(new CycleParameterImpl(configuration, key, VALUE, cycle)));
		assertFalse(cycleParameter.equals(new CycleParameterImpl(configuration, key, VALUE, Mockito.mock(Cycle.class))));
		assertFalse(cycleParameter.equals(new CycleParameterImpl(configuration, Key.DaysBack, VALUE, cycle)));

		final var otherParameter = new CycleParameterImpl(configuration, key, VALUE, cycle);

		ReflectionTestUtils.setField(otherParameter, CYCLE_FIELD_NAME, null);

		assertFalse(otherParameter.equals(cycleParameter));
		assertFalse(cycleParameter.equals(otherParameter));
	}

}
