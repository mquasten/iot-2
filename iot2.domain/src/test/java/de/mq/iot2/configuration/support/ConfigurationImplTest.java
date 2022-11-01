package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.support.RandomTestUtil;

class ConfigurationImplTest {

	private static final String NAME_FIELD_NAME = "name";
	private static final String KEY_FIELD_NAME = "key";
	private static final String ID_FIELD_NAME = "id";
	private final long id = RandomTestUtil.randomLong();
	private final String name = RandomTestUtil.randomString();

	@Test
	void create() {
		final Configuration configuration = new ConfigurationImpl(id, RuleKey.EndOfDay, name);

		assertEquals(RuleKey.EndOfDay, configuration.key());
		assertEquals(name, configuration.name());
		assertEquals(new UUID(id, id).toString(), ReflectionTestUtils.getField(configuration, ID_FIELD_NAME));
	}

	@Test
	void createInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new ConfigurationImpl(id, null, name));
		assertThrows(IllegalArgumentException.class, () -> new ConfigurationImpl(id, RuleKey.EndOfDay, null));
	}

	@Test
	void createWithRandomId() {
		final Configuration configuration = new ConfigurationImpl(RuleKey.EndOfDay, name);

		assertEquals(RuleKey.EndOfDay, configuration.key());
		assertEquals(name, configuration.name());
		final var uuid = ReflectionTestUtils.getField(configuration, ID_FIELD_NAME);
		assertNotNull(uuid);
		assertNotEquals(new UUID(id, id).toString(), uuid);
	}

	@Test
	void createWithRandomIdInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new ConfigurationImpl(null, name));
		assertThrows(IllegalArgumentException.class, () -> new ConfigurationImpl(RuleKey.EndOfDay, null));
	}

	@Test
	void key() {
		final Configuration configuration = BeanUtils.instantiateClass(ConfigurationImpl.class);
		assertThrows(IllegalArgumentException.class, () -> configuration.key());

		ReflectionTestUtils.setField(configuration, KEY_FIELD_NAME, RuleKey.EndOfDay);
		assertEquals(RuleKey.EndOfDay, configuration.key());
	}

	@Test
	void name() {
		final Configuration configuration = BeanUtils.instantiateClass(ConfigurationImpl.class);
		assertTrue(configuration.name().isEmpty());

		ReflectionTestUtils.setField(configuration, NAME_FIELD_NAME, name);
		assertEquals(name, configuration.name());
	}

	@Test
	void hash() {
		assertEquals(System.identityHashCode(RuleKey.EndOfDay), new ConfigurationImpl(RuleKey.EndOfDay, name).hashCode());
	}

	@Test
	void hashInvalid() {
		final Configuration configuration = BeanUtils.instantiateClass(ConfigurationImpl.class);
		assertEquals(System.identityHashCode(configuration), configuration.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	final void equals() {
		final Configuration infalidConfiguration = BeanUtils.instantiateClass(ConfigurationImpl.class);

		assertTrue(infalidConfiguration.equals(infalidConfiguration));
		Configuration validConfiguration = new ConfigurationImpl(RuleKey.EndOfDay, name);
		assertFalse(validConfiguration.equals(infalidConfiguration));
		assertFalse(infalidConfiguration.equals(validConfiguration));
		assertTrue(validConfiguration.equals(new ConfigurationImpl(RuleKey.EndOfDay, name)));
		assertFalse(validConfiguration.equals(new ConfigurationImpl(RuleKey.CleanUp, name)));
		assertFalse(validConfiguration.equals(name));

	}

}
