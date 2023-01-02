package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DayModelTest {

	private final DayModel dayModel= new DayModel();
	
	@Test
	void id() {
		assertNull(dayModel.getId());
		
		final var id = randomString();
		dayModel.setId(id);
		
		assertEquals(id, dayModel.getId());
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}
	
	@Test
	void value() {
		assertNull(dayModel.getValue());
		
		final var value = randomString();
		dayModel.setValue(value);
		
		assertEquals(value, dayModel.getValue());
	}
	
	@Test
	void valueSorted() {
		assertNull(sortedValue());
		
		final var valueSorted = randomString();
		dayModel.setValueSorted(valueSorted);
		
		assertEquals(valueSorted, sortedValue());		
	}

	private Object sortedValue() {
		return ReflectionTestUtils.getField(dayModel, "valueSorted");
	}
	
	@Test
	void dayGroupId() {
		assertNull(dayModel.getDayGroupId());
		
		final var dayGroupId = randomString();
		dayModel.setDayGroupId(dayGroupId);
		
		assertEquals(dayGroupId, dayModel.getDayGroupId());
	}
	
	@Test
	void type() {
		assertNull(dayModel.getType());
		
		final var type = randomString();
		dayModel.setType(type);
		
		assertEquals(type, dayModel.getType());
	}
	
	@Test
	void targetValue() {
		assertNull(dayModel.getTargetValue());
		
		final var targetValue = randomString();
		dayModel.setTargetValue(targetValue);
		
		assertEquals(targetValue, dayModel.getTargetValue());
	}
	
	@Test
	void description() {
		assertNull(dayModel.getDescription());
		
		final var description = randomString();
		dayModel.setDescription(description);
		
		assertEquals(description, dayModel.getDescription());
	}
	
	@Test 
	void targetEntity() {
		dayModel.setType(LocalDateDayImp.class.getName());
		
		assertEquals(LocalDateDayImp.class, dayModel.targetEntity());
	}
	
	@Test 
	void targetEntityCanNotCreate() {
		dayModel.setType(randomString());
		
		assertThrows(IllegalStateException.class, () -> dayModel.targetEntity());
	}
	@Test 
	void targetEntityTypeMissing() {
		
		assertThrows(IllegalArgumentException.class, () -> dayModel.targetEntity());
	}
	
	@Test 
	void compareTo() {
		final var valueSorted = randomString();
		dayModel.setValueSorted(valueSorted);
		final var other = new DayModel();
		other.setValueSorted(valueSorted);
		
		assertEquals(0, dayModel.compareTo(other));
		
		other.setValueSorted(randomString());
		int result = dayModel.compareTo(other);
		
		assertTrue(result!=0);
		if(result < 0) {
			assertTrue(other.compareTo(dayModel)>0);
		} else {
			assertTrue(other.compareTo(dayModel)<0);
		}
	}
}
