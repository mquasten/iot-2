package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class DayGroupImplTest {

	private static final String ID_FIELD_NAME = "id";

	private static final Long ID = 1L;

	private static final String GROUP_NAME = "Feiertage";
	

	@Test
	final void create() {
		final var dayGroup = new DayGroupImpl(ID, GROUP_NAME, false);
		assertEquals(GROUP_NAME, dayGroup.name());
		assertFalse(dayGroup.readOnly());
		assertEquals(new UUID(ID, ID).toString(), ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME));
	}

	@Test
	final void createReadOnlyWithRandomId() {
		final var dayGroup = newDayGroup();
		assertEquals(GROUP_NAME, dayGroup.name());
		assertTrue(dayGroup.readOnly());
		assertEquals(GROUP_NAME, dayGroup.name());
		final var id = ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME);
		assertNotEquals(new UUID(ID, ID), id);
		assertNotNull(id);
	}

	@Test
	final void createRandomId() {
		final var dayGroup = new DayGroupImpl(GROUP_NAME, false);
		assertEquals(GROUP_NAME, dayGroup.name());
		assertFalse(dayGroup.readOnly());
		final var id = ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME);
		assertNotEquals(new UUID(ID, ID), id);
		assertNotNull(id);
	}

	@Test
	final void createRandomIdReadOnly() {
		final var dayGroup = new DayGroupImpl(GROUP_NAME, true);
		assertEquals(GROUP_NAME, dayGroup.name());
		assertTrue(dayGroup.readOnly());
		final var id = ReflectionTestUtils.getField(dayGroup, ID_FIELD_NAME);
		assertNotEquals(new UUID(ID, ID), id);
		assertNotNull(id);
	}

	
	
	private DayGroupImpl newDayGroup() {
		return new DayGroupImpl(GROUP_NAME);
	}


	@Test
	final void hash() {
		assertEquals(GROUP_NAME.hashCode(), newDayGroup().hashCode());
		final var dayGroup = BeanUtils.instantiateClass(DayGroupImpl.class);
		assertEquals(System.identityHashCode(dayGroup), dayGroup.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	final void equals() {
		assertTrue(newDayGroup().equals(newDayGroup()));
		assertTrue(newDayGroup().equals(new DayGroupImpl(ID, GROUP_NAME.toUpperCase())));
		assertFalse(newDayGroup().equals(new DayGroupImpl(ID, "x")));
		assertFalse(newDayGroup().equals(GROUP_NAME));

		final var dayGroup = newDayGroup();
		final var otherDayGroup = BeanUtils.instantiateClass(DayGroupImpl.class);

		assertFalse(dayGroup.equals(otherDayGroup));
		assertFalse(otherDayGroup.equals(dayGroup));

		assertTrue(otherDayGroup.equals(otherDayGroup));
	}

}
