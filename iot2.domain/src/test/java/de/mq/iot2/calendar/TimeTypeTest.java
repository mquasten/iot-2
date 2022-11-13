package de.mq.iot2.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.mq.iot2.calendar.CalendarService.TimeType;

class TimeTypeTest {
	
	@Test
	void key() {
		Arrays.asList(TimeType.values()).forEach(timeType -> assertEquals(timeType.name().toUpperCase(),timeType.key()));
	}
	
	@Test
	void offset() {
		assertEquals(1, TimeType.Winter.offset());
		assertEquals(2, TimeType.Summer.offset());
	}

}
