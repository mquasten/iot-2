package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Day;

class DayGroupImplTest {

	private static final Long ID = 1L;

	private static final String DAYS_FIELD_NAME = "days";

	private static final String GROUP_NAME = "Feiertage";

	private final Day<?> day = Mockito.mock(Day.class);

	@Test
	final void create() {
		final var dayGroup = new DayGroupImpl(ID, GROUP_NAME, false);
		assertEquals(GROUP_NAME, dayGroup.name());
		assertFalse(dayGroup.readOnly());
		assertEquals(0, dayGroup.days().size());
		assertEquals(new UUID(ID, ID).toString(), ReflectionTestUtils.getField(dayGroup, "id"));
	}

	@Test
	final void createReadOnlyWithRandomId() {
		final var dayGroup = newDayGroup();
		assertEquals(GROUP_NAME, dayGroup.name());
		assertTrue(dayGroup.readOnly());
		assertEquals(0, dayGroup.days().size());
		assertEquals(GROUP_NAME, dayGroup.name());
		final var id = ReflectionTestUtils.getField(dayGroup, "id");
		assertNotEquals(new UUID(ID, ID), id);
		assertNotNull(id);
	}

	@Test
	final void createRandomId() {
		final var dayGroup = new DayGroupImpl(GROUP_NAME, false);
		assertEquals(GROUP_NAME, dayGroup.name());
		assertFalse(dayGroup.readOnly());
		assertEquals(0, dayGroup.days().size());
		final var id = ReflectionTestUtils.getField(dayGroup, "id");
		assertNotEquals(new UUID(ID, ID), id);
		assertNotNull(id);
	}

	@Test
	final void createRandomIdReadOnly() {
		final var dayGroup = new DayGroupImpl(GROUP_NAME, true);
		assertEquals(GROUP_NAME, dayGroup.name());
		assertTrue(dayGroup.readOnly());
		assertEquals(0, dayGroup.days().size());
		final var id = ReflectionTestUtils.getField(dayGroup, "id");
		assertNotEquals(new UUID(ID, ID), id);
		assertNotNull(id);
	}

	@Test
	final void assign() {
		final var dayGroup = newDayGroup();
		assertEquals(0, dayGroup.days().size());

		dayGroup.assign(day);

		assertEquals(1, dayGroup.days().size());

		assertTrue(new HashSet<>(Set.of(day)).equals(new HashSet<>(dayGroup.days())));
	}

	@Test
	final void remove() {
		final var dayGroup = newDayGroup();
		assignDays(dayGroup);
		assertEquals(1, dayGroup.days().size());

		dayGroup.remove(day);

		assertEquals(0, dayGroup.days().size());
	}

	private DayGroupImpl newDayGroup() {
		return new DayGroupImpl(GROUP_NAME);
	}

	private void assignDays(final DayGroupImpl dayGroup) {
		ReflectionTestUtils.setField(dayGroup, DAYS_FIELD_NAME, new HashSet<>(Set.of(day)));
	}

	@Test
	final void days() {
		final var dayGroup = newDayGroup();
		assignDays(dayGroup);
		assertTrue(new HashSet<>(Set.of(day)).equals(new HashSet<>(dayGroup.days())));
	}

	@Test
	final void daysCollectionUnmodifyable() {
		final var dayGroup = newDayGroup();
		assignDays(dayGroup);

		assertThrows(UnsupportedOperationException.class, () -> dayGroup.days().clear());
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
