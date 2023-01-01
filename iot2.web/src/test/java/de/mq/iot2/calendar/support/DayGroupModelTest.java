package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

class DayGroupModelTest {
	
	private final DayGroupModel dayGroupModel = new DayGroupModel();
	
	@Test
	void id() {
		assertNull(dayGroupModel.getId());
		
		final String id = randomString();
		dayGroupModel.setId(id);
		
		assertEquals(id, dayGroupModel.getId());
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}
	
	@Test
	void name() {
		assertNull(dayGroupModel.getName());
		
		final String name = randomString();
		dayGroupModel.setName(name);
		
		assertEquals(name, dayGroupModel.getName());
	}
	
	@Test
	void readonly() {
		assertFalse(dayGroupModel.isReadonly());
		
		dayGroupModel.setReadonly(true);
		
		assertTrue(dayGroupModel.isReadonly());
	}
	
	@Test
	void cycleId() {
		assertNull(dayGroupModel.getCycleId());
		
		final String cycleId = randomString();
		dayGroupModel.setCycleId(cycleId);
		
		assertEquals(cycleId, dayGroupModel.getCycleId());
		
	}
	
	@Test
	void days() {
		assertTrue(CollectionUtils.isEmpty(dayGroupModel.getDays()));
		
		final Collection<DayModel> days = List.of(new DayModel());
		dayGroupModel.setDays(days);
		
		assertEquals(days, dayGroupModel.getDays());
		
	}

}
