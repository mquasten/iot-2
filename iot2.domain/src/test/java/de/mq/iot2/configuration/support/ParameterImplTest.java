package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.RandomTestUtil;

class ParameterImplTest {
	private static final String ID_FIELD_NAME = "id";
	private final Configuration configuration = Mockito.mock(Configuration.class);
	private static final String VALUE = RandomTestUtil.randomString();

	private static final UUID CONFIGURATION_ID = UUID.randomUUID();

	@BeforeEach
	void setup() {
		Mockito.when(configuration.id()).thenReturn(CONFIGURATION_ID);
	}

	@ParameterizedTest
	@EnumSource(value = Key.class)
	void create(final Key key) {
		final var parameter = new ParameterImpl(configuration, key, VALUE);

		assertEquals(configuration, parameter.configuration());
		assertEquals(VALUE, parameter.value());
		assertEquals(key, parameter.key());
		assertEquals(expectedId(key), ReflectionTestUtils.getField(parameter, ID_FIELD_NAME));

	}

	private String expectedId(final Key key) {
		final var uuid = UUID.nameUUIDFromBytes(key.name().getBytes());
		return new UUID(uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits(), configuration.id().getLeastSignificantBits() + configuration.id().getMostSignificantBits()).toString();

	}

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

}
