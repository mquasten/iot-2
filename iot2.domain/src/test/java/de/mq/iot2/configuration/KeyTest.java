package de.mq.iot2.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.configuration.Parameter.Key;

class KeyTest {

	@Test
	void type() {
		Arrays.asList(Key.values()).stream().filter(key -> (key != Key.DaysBack) && key != Key.SunUpDownType && key != Key.ShadowTemperature)
				.forEach(key -> assertEquals(LocalTime.class, key.type()));
		Arrays.asList(Key.values()).stream().filter(key -> key == Key.DaysBack).forEach(key -> assertEquals(Integer.class, key.type()));
		Arrays.asList(Key.values()).stream().filter(key -> key == Key.SunUpDownType).forEach(key -> assertEquals(TwilightType.class, key.type()));
		Arrays.asList(Key.values()).stream().filter(key -> key == Key.ShadowTemperature).forEach(key -> assertEquals(Double.class, key.type()));
	}

}
