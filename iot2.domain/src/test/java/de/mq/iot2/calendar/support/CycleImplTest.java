package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;

class CycleImplTest {

	private static final String PRIORITY_FIELD_NAME = "priority";
	private static final String NAME_FIELD_NAME = "name";
	private static final String ID_FIELD_NAME = "id";
	private final long id = RandomTestUtil.randomLong();
	private final String name = RandomTestUtil.randomString();
	private final int priority = RandomTestUtil.randomInt();

	@Test
	void create() {
		final var cycle = new CycleImpl(id, name, priority);

		assertEquals(name, cycle.name());
		assertEquals(priority, cycle.priority());
		assertEquals(IdUtil.id(id), ReflectionTestUtils.getField(cycle, ID_FIELD_NAME));
		assertFalse(cycle.isDeaultCycle());
	}

	@Test
	void createWithDefaultCycle() {
		final var cycle = new CycleImpl(id, name, priority, true);

		assertEquals(name, cycle.name());
		assertEquals(priority, cycle.priority());
		assertEquals(IdUtil.id(id), ReflectionTestUtils.getField(cycle, ID_FIELD_NAME));
		assertTrue(cycle.isDeaultCycle());
	}

	@Test
	void createRandomId() {
		final var cycle = new CycleImpl(name, priority);

		assertEquals(name, cycle.name());
		assertEquals(priority, cycle.priority());
		assertNotNull(ReflectionTestUtils.getField(cycle, ID_FIELD_NAME));
		assertNotEquals(IdUtil.id(id), ReflectionTestUtils.getField(cycle, ID_FIELD_NAME));
		assertFalse(cycle.isDeaultCycle());
	}

	@Test
	void createRandomIdithDefaultCycle() {
		final var cycle = new CycleImpl(name, priority, true);

		assertEquals(name, cycle.name());
		assertEquals(priority, cycle.priority());
		assertNotNull(ReflectionTestUtils.getField(cycle, ID_FIELD_NAME));
		assertNotEquals(IdUtil.id(id), ReflectionTestUtils.getField(cycle, ID_FIELD_NAME));
		assertTrue(cycle.isDeaultCycle());
	}

	@ParameterizedTest
	@EmptySource
	@NullSource
	@ValueSource(strings = { " " })
	void createNameEmpty(final String value) {
		assertThrows(IllegalArgumentException.class, () -> new CycleImpl(value, priority));
	}

	@Test
	void name() {
		final var cycle = newWithDefaultConstructor();

		assertTrue(cycle.name().isEmpty());

		ReflectionTestUtils.setField(cycle, NAME_FIELD_NAME, name);
		assertEquals(name, cycle.name());
	}

	@Test
	void priority() {
		final var cycle = newWithDefaultConstructor();

		assertEquals(Integer.MAX_VALUE, cycle.priority());

		ReflectionTestUtils.setField(cycle, PRIORITY_FIELD_NAME, priority);
		assertEquals(priority, cycle.priority());
	}

	@Test
	void hash() {
		final var cycle = newWithDefaultConstructor();

		assertEquals(System.identityHashCode(cycle), cycle.hashCode());

		ReflectionTestUtils.setField(cycle, NAME_FIELD_NAME, name);
		assertEquals(name.hashCode(), cycle.hashCode());
	}

	private CycleImpl newWithDefaultConstructor() {
		return BeanUtils.instantiateClass(CycleImpl.class);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void equals() {

		final var cycle = newWithDefaultConstructor();
		final var other = newWithDefaultConstructor();

		assertTrue(cycle.equals(cycle));
		assertFalse(cycle.equals(other));
		assertFalse(cycle.equals(NAME_FIELD_NAME));

		ReflectionTestUtils.setField(cycle, NAME_FIELD_NAME, name);
		assertFalse(cycle.equals(other));
		assertFalse(other.equals(cycle));

		ReflectionTestUtils.setField(other, NAME_FIELD_NAME, name);
		assertTrue(cycle.equals(other));

		ReflectionTestUtils.setField(cycle, NAME_FIELD_NAME, RandomTestUtil.randomString());
		assertFalse(cycle.equals(other));
	}

	@Test
	void string() {
		assertEquals(name, new CycleImpl(id, name, priority).toString());
	}

	@Test
	void stringNameNull() {
		final var cycle = newWithDefaultConstructor();
		assertEquals(CycleImpl.class.getName() + "@" + Integer.toHexString(System.identityHashCode(cycle)), cycle.toString());
	}
}
