package de.mq.iot2.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.mq.iot2.calendar.CalendarService.TwilightType;

class TwilightTypeTest {

	@Test
	void length() {
		Arrays.asList(TwilightType.values()).forEach(value -> assertTrue(value.horizonElevationInDegrees() < 0d));
	}

	@Test
	void horizonElevationInDegreesMathematical() {
		assertEquals(-50d / 60, TwilightType.Mathematical.horizonElevationInDegrees());
	}

	@Test
	void horizonElevationInDegreesCivil() {
		assertEquals(-6d, TwilightType.Civil.horizonElevationInDegrees());
	}

	@Test
	void horizonElevationInDegreesNautical() {
		assertEquals(-12d, TwilightType.Nautical.horizonElevationInDegrees());
	}

	@Test
	void horizonElevationInDegreesAstronomical() {
		assertEquals(-18d, TwilightType.Astronomical.horizonElevationInDegrees());
	}

}
