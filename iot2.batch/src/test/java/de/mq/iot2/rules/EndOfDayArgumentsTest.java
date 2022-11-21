package de.mq.iot2.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class EndOfDayArgumentsTest {

	@Test
	void endOfDayArguments() {
		Arrays.asList(EndOfDayArguments.values()).forEach(value -> assertEquals(value, EndOfDayArguments.valueOf(value.name())));
	}

}
