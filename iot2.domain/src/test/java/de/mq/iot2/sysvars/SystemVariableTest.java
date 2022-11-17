package de.mq.iot2.sysvars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.mq.iot2.support.RandomTestUtil;

class SystemVariableTest {
	
	private SystemVariable systemVariable = new SystemVariable();
	
	@Test
	void name() {
		assertNull(systemVariable.getName());
		
		final var name = RandomTestUtil.randomString();
		systemVariable.setName(name);
		
		assertEquals(name, systemVariable.getName());
	}
	
	@Test
	void value() {
		assertNull(systemVariable.getValue());
		
		final var value = RandomTestUtil.randomString();
		systemVariable.setValue(value);
		
		assertEquals(value, systemVariable.getValue());
	}
	
	@Test
	void id() {
		assertNull(systemVariable.getId());
		
		final var id = ""+ RandomTestUtil.randomInt();
		systemVariable.setId(id);
		
		assertEquals(id, systemVariable.getId());
	}

}
