package de.mq.iot2.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import de.mq.iot2.configuration.Parameter.Key;

class KeyTest {

	@Test
	void type() {
		Arrays.asList(Key.values()).stream().filter(key -> key != Key.DaysBack).forEach(key -> assertEquals(LocalTime.class, key.type()));
		Arrays.asList(Key.values()).stream().filter(key -> key == Key.DaysBack).forEach(key -> assertEquals(Integer.class, key.type()));
	}

}
