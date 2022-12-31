package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.support.IdUtil;

class CycleMapperTest {

	private final CycleMapper cycleMapper = new CycleMapper();

	private final Cycle cycle = new CycleImpl(1L, UUID.randomUUID().toString(), 0);

	@Test
	void toWeb() {
		final var cycleModel = cycleMapper.toWeb(cycle);

		assertEquals(IdUtil.getId(cycle), cycleModel.getId());
		assertEquals(cycle.name(), cycleModel.getName());
	}

}
