package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.RandomTestUtil;

class ParameterImplTest {
	private final Configuration configuration = Mockito.mock(Configuration.class);
	private static final String VALUE = RandomTestUtil.randomString();

	@Test
	void createConstructorWithoutArgs() {
		assertTrue(BeanUtils.instantiateClass(ParameterImpl.class) instanceof Parameter);
	}

	@Test
	void createInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new ParameterImpl(null, Key.DaysBack, VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ParameterImpl(configuration, null, VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ParameterImpl(configuration, Key.DaysBack, null));
	}

	@Test
	void create() {
		final var parameter = new ParameterImpl(configuration, Key.DaysBack, VALUE);
		assertEquals(configuration, parameter.configuration());
		assertEquals(Key.DaysBack, parameter.key());
		assertEquals(VALUE, parameter.value());
	}

}
