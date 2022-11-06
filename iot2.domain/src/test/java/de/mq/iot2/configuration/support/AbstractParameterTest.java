package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;

class AbstractParameterTest { 

	private static final String CONFIGURATION_FIELD_NAME = "configuration";
	private static final String VALUE_FIELD_NAME = "value";
	private static final String KEY_FIELD_NAME = "key";
	private static final String ID_FIELD_NAME = "id";
	private static final String VALUE = RandomTestUtil.randomString();
	private final Configuration configuration = Mockito.mock(Configuration.class);


	
	@Test
	void create() {
		final Key key= Key.MaxSunUpTime;
		final var parameter = newParameter(configuration, key, VALUE);
		assertEquals(VALUE, parameter.value());
		assertEquals(key, parameter.key());
		assertEquals(configuration, parameter.configuration());
		assertEquals(compareableDigitsFromTimestamp(IdUtil.id()), compareableDigitsFromTimestamp((String) ReflectionTestUtils.getField(parameter, ID_FIELD_NAME)));
	}
	
	private String compareableDigitsFromTimestamp(final String uuid) {
		final var values = uuid.split("[-]");
		final var last = values[values.length - 1];
		return values[values.length - 2] +"-"+ last.substring(0, last.length() - 2);
	}

	@Test
	void createInvalid() {
		assertThrows(IllegalArgumentException.class, () -> newParameter(null, Key.DaysBack, VALUE));
		assertThrows(IllegalArgumentException.class, () -> newParameter(configuration, null, VALUE));
		assertThrows(IllegalArgumentException.class, () -> newParameter(configuration, Key.DaysBack, null));
	}

	@Test
	void key() {
		final var parameter = newParameter();

		assertThrows(IllegalArgumentException.class, () -> parameter.key());

		final var key = Key.DaysBack;
		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, key);
		assertEquals(key, parameter.key());
	}

	@Test
	void value() {
		final var parameter = newParameter();
		assertThrows(IllegalArgumentException.class, () -> parameter.value());

		ReflectionTestUtils.setField(parameter, VALUE_FIELD_NAME, VALUE);
		assertEquals(VALUE, parameter.value());
	}

	@Test
	void configuaration() {
		final var parameter = newParameter();
		assertThrows(IllegalArgumentException.class, () -> parameter.configuration());

		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, configuration);
		assertEquals(configuration, parameter.configuration());
	}

	@Test
	void hash() {
		final var key = Key.DaysBack;
		final var parameter = newParameter();
		assertEquals(System.identityHashCode(parameter), parameter.hashCode());

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, key);
		assertEquals(System.identityHashCode(parameter), parameter.hashCode());

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, null);
		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, configuration);
		assertEquals(System.identityHashCode(parameter), parameter.hashCode());

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, key);
		assertEquals(key.hashCode() + configuration.hashCode() + parameter.getClass().hashCode(), parameter.hashCode());
	}

	@Test
	void equals() {
		final var parameter = newParameter();
		final var otherParameter = newParameter();
		assertFalse(parameter.equals(VALUE));

		assertFalse(parameter.equals(otherParameter));
		assertTrue(parameter.equals(parameter));
		assertTrue(otherParameter.equals(otherParameter));

		ReflectionTestUtils.setField(otherParameter, KEY_FIELD_NAME, Key.UpTime);
		ReflectionTestUtils.setField(otherParameter, CONFIGURATION_FIELD_NAME, configuration);

		assertFalse(parameter.equals(otherParameter));
		assertFalse(otherParameter.equals(parameter));

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, Key.UpTime);
		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, configuration);

		assertTrue(otherParameter.equals(parameter));

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, Key.MinSunDownTime);

		assertFalse(otherParameter.equals(parameter));

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, Key.UpTime);
		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, Mockito.mock(Configuration.class));

		assertFalse(otherParameter.equals(parameter));

		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, configuration);
		final var parameterOtherClass = new AbstractParameter(configuration, Key.UpTime, VALUE) {
		};

		assertFalse(parameterOtherClass.equals(parameter));
	}

	private Parameter newParameter(final Configuration configuration, final Key key, final String value) {

		return new AbstractParameter(configuration, key, value) {
		};
	}

	private Parameter newParameter() {
		return new AbstractParameter() {
		};

	}

}
