package de.mq.iot2.sysvars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	@Test
	void defaultConstructor() {
		final  var systemVariable = new SystemVariable();
		assertNull(systemVariable.getId());
		assertNull(systemVariable.getName());
		assertNull(systemVariable.getValue());
	}
	
	@Test
	void constructor() {
		final var name= RandomTestUtil.randomString();
		final var value= RandomTestUtil.randomString();
		
		final  var systemVariable = new SystemVariable(name,value );
		
		assertNull(systemVariable.getId());
		assertEquals(name,systemVariable.getName());
		assertEquals(value,systemVariable.getValue());
	}
	
	@Test
	void constructorNullValues() {
		assertThrows(IllegalArgumentException.class,() -> new SystemVariable(null, RandomTestUtil.randomString()));
		assertThrows(IllegalArgumentException.class,() -> new SystemVariable(RandomTestUtil.randomString(), null));
	}

}
