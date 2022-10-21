package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.MonthDay;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DayOfMonthImplTest {
	
	private static final String ID_FIELD = "id";
	private static final String VALUE_FIELD = "value";
	private static final String DESCRIPTION = "Weihnachten";

	@Test
	void createEntity() {
		final var monthDay = MonthDay.of(12, 25);
		final var day = new DayOfMonthImpl(monthDay, DESCRIPTION);
		
		assertEquals(monthDay,day.value());
		assertTrue(day.description().isPresent());
		assertEquals(DESCRIPTION, day.description().get());
		final var  expectedValue = monthDay.getMonthValue()*100+ monthDay.getDayOfMonth();
		assertEquals(expectedValue, ReflectionTestUtils.getField(day, VALUE_FIELD));
		assertEquals(new UUID(DayOfMonthImpl.ENTITY_NAME.hashCode(),expectedValue).toString(), ReflectionTestUtils.getField(day, ID_FIELD));
	}

}
