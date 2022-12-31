package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CycleModelTest {

	private final CycleModel cycleModel = new CycleModel();

	@Test
	void id() {
		assertNull(cycleModel.getId());

		final var id = random();
		cycleModel.setId(id);

		assertEquals(id, cycleModel.getId());
	}

	@Test
	void name() {
		assertNull(cycleModel.getName());

		final var name = random();
		cycleModel.setName(name);

		assertEquals(name, cycleModel.getName());
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

}
