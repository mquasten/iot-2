package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.support.RandomTestUtil;

class DayGroupImplTest {

	private static final String NAME_FIELD_NAME = "name";

	private static final String ID_FIELD_NAME = "id";

	private final Long id = RandomTestUtil.randomLong();

	private final String name = RandomTestUtil.randomString();

	private final Cycle cycle = Mockito.mock(Cycle.class);

	@Test
	final void create() {
		final var dayGroup = new DayGroupImpl(cycle, id, name, false);
		assertEquals(name, dayGroup.name());
		assertFalse(dayGroup.readOnly());
		assertEquals(new UUID(id, id).toString(), ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME));
		assertEquals(cycle, dayGroup.cycle());
	}

	@Test
	final void createReadOnlyWithRandomId() {
		final var dayGroup = newDayGroup();
		assertEquals(name, dayGroup.name());
		assertTrue(dayGroup.readOnly());
		assertEquals(name, dayGroup.name());
		final var id = ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME);
		assertNotEquals(new UUID(this.id, this.id), id);
		assertNotNull(id);
		assertEquals(cycle, dayGroup.cycle());
	}

	@Test
	final void createRandomId() {
		final var dayGroup = new DayGroupImpl(cycle, name, false);
		assertEquals(name, dayGroup.name());
		assertFalse(dayGroup.readOnly());
		final var id = ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME);
		assertNotEquals(new UUID(this.id, this.id), id);
		assertNotNull(id);
		assertEquals(cycle, dayGroup.cycle());
	}

	@Test
	final void createRandomIdReadOnly() {
		final var dayGroup = new DayGroupImpl(cycle, name, true);
		assertEquals(name, dayGroup.name());
		assertTrue(dayGroup.readOnly());
		final var id = ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME);
		assertNotEquals(new UUID(this.id, this.id), id);
		assertNotNull(id);
		assertEquals(cycle, dayGroup.cycle());
	}

	private DayGroupImpl newDayGroup() {
		return new DayGroupImpl(cycle, name);
	}

	@Test
	final void name() {
		final var dayGroup = BeanUtils.instantiateClass(DayGroupImpl.class);

		assertTrue(dayGroup.name().isEmpty());

		ReflectionTestUtils.setField(dayGroup, NAME_FIELD_NAME, name);
		assertEquals(name, dayGroup.name());
	}

	@Test
	final void hash() {
		assertEquals(name.hashCode(), newDayGroup().hashCode());
		final var dayGroup = BeanUtils.instantiateClass(DayGroupImpl.class);
		assertEquals(System.identityHashCode(dayGroup), dayGroup.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	final void equals() {
		assertTrue(newDayGroup().equals(newDayGroup()));
		assertTrue(newDayGroup().equals(new DayGroupImpl(cycle, id, name.toUpperCase())));
		assertFalse(newDayGroup().equals(new DayGroupImpl(cycle, id, "x")));
		assertFalse(newDayGroup().equals(name));

		final var dayGroup = newDayGroup();
		final var otherDayGroup = BeanUtils.instantiateClass(DayGroupImpl.class);

		assertFalse(dayGroup.equals(otherDayGroup));
		assertFalse(otherDayGroup.equals(dayGroup));

		assertTrue(otherDayGroup.equals(otherDayGroup));
	}

}
