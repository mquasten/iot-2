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

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;

class ParameterImplTest {

	private static final String CONFIGURATION_FIELD_NAME = "configuration";
	private static final String VALUE_FIELD_NAME = "value";
	private static final String KEY_FIELD_NAME = "key";
	private static final String ID_FIELD_NAME = "id";
	private static final String VALUE = RandomTestUtil.randomString();
	private final Configuration configuration = Mockito.mock(Configuration.class);

	@ParameterizedTest
	@EnumSource(Key.class)
	void create(final Key key) {
		final var parameter = new ParameterImpl(configuration, key, VALUE);
		assertEquals(VALUE, parameter.value());
		assertEquals(key, parameter.key());
		assertEquals(configuration, parameter.configuration());
		assertEquals(compareableDigitsFromTimestamp(IdUtil.id()), compareableDigitsFromTimestamp((String) ReflectionTestUtils.getField(parameter, ID_FIELD_NAME)));
	}

	private String compareableDigitsFromTimestamp(final String uuid) {
		final var values = uuid.split("[-]");
		final var last = values[values.length - 1];
		return values[values.length - 2] + "-" + last.substring(0, last.length() - 2);
	}

	@Test
	void createInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new ParameterImpl(null, Key.DaysBack, VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ParameterImpl(configuration, null, VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ParameterImpl(configuration, Key.DaysBack, null));
	}

	@Test
	void key() {
		final var parameter = new ParameterImpl();
		assertThrows(IllegalArgumentException.class, () -> parameter.key());

		final var key = Key.DaysBack;
		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, key);
		assertEquals(key, parameter.key());
	}

	@Test
	void value() {
		final var parameter = new ParameterImpl();
		assertThrows(IllegalArgumentException.class, () -> parameter.value());

		ReflectionTestUtils.setField(parameter, VALUE_FIELD_NAME, VALUE);
		assertEquals(VALUE, parameter.value());
	}

	@Test
	void configuaration() {
		final var parameter = new ParameterImpl();
		assertThrows(IllegalArgumentException.class, () -> parameter.configuration());

		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, configuration);
		assertEquals(configuration, parameter.configuration());
	}

	@Test
	void hash() {
		final var key = Key.DaysBack;
		final var parameter = new ParameterImpl();
		assertEquals(System.identityHashCode(parameter), parameter.hashCode());

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, key);
		assertEquals(System.identityHashCode(parameter), parameter.hashCode());

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, null);
		ReflectionTestUtils.setField(parameter, CONFIGURATION_FIELD_NAME, configuration);
		assertEquals(System.identityHashCode(parameter), parameter.hashCode());

		ReflectionTestUtils.setField(parameter, KEY_FIELD_NAME, key);
		assertEquals(key.hashCode() + configuration.hashCode() + ParameterImpl.class.hashCode(), parameter.hashCode());
	}

	@Test
	void equals() throws NoSuchMethodException, SecurityException {
		final var parameter = new ParameterImpl();
		final var otherParameter = new ParameterImpl();
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
		final var parameterOtherClass = createParameterMockWithOtherClass();

		assertFalse(parameterOtherClass.equals(parameter));
	}

	private ParameterImpl createParameterMockWithOtherClass() throws NoSuchMethodException, SecurityException {
		final var mock = Mockito.mock(ParameterImpl.class);
		final var constructor = mock.getClass().getDeclaredConstructor(Configuration.class, Key.class, String.class);
		final var parameterOtherClass = BeanUtils.instantiateClass(constructor, configuration, Key.UpTime, VALUE);
		return parameterOtherClass;

	}

}
