package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import de.mq.iot2.calendar.CalendarService.TwilightType;

public class SunUpDownCalculatorImplTest {
	
	
	
	@Test
	final void sunDownTime() {
		assertEquals(LocalTime.of(19, 57), new SunUpDownCalculatorImpl(TwilightType.Mathematical).sunDownTime(86, 2));
	}

	@Test
	final void sunUpTime() {
		assertEquals(LocalTime.of(7, 23), new SunUpDownCalculatorImpl(TwilightType.Mathematical).sunUpTime(86, 2));
	}
	
	@Test
	final void sunUpBerlin() {
		assertEquals(LocalTime.of(7, 50), new SunUpDownCalculatorImpl(52.5,13.5, TwilightType.Mathematical).sunUpTime(30, 1));
	}
	
	
	@Test
	final  void  sunUpTime0Minutes()  {
		// am 17.10, 289. Tag sind es im Algorithmus 60 Minuten -> plus eine Stunde , 0 Minuten.
		assertEquals(LocalTime.of(8, 0), new SunUpDownCalculatorImpl(TwilightType.Mathematical).sunUpTime(289, 2));
	}

}
