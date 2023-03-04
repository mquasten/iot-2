package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.mq.iot2.calendar.CalendarService.TwilightType;

public class SunUpDownCalculatorImplTest {

	@Test
	final void sunDownTime() {
		assertEquals(Optional.of(LocalTime.of(19, 57)), newSunUpDownCalculator(TwilightType.Mathematical).sunDownTime(86, 2));
	}

	private SunUpDownCalculatorImpl newSunUpDownCalculator(final TwilightType twilightType) {
		return new SunUpDownCalculatorImpl(51.1423399, 6.2815922, twilightType);
	}

	@Test
	final void sunUpTime() {
		assertEquals(Optional.of(LocalTime.of(7, 23)), newSunUpDownCalculator(TwilightType.Mathematical).sunUpTime(86, 2));
	}

	@Test
	final void sunUpBerlin() {
		assertEquals(Optional.of(LocalTime.of(7, 50)), new SunUpDownCalculatorImpl(52.5, 13.5, TwilightType.Mathematical).sunUpTime(30, 1));
	}

	@Test
	final void sunUpTime0Minutes() {
		// am 17.10, 289. Tag sind es im Algorithmus 60 Minuten -> plus eine Stunde , 0
		// Minuten.
		assertEquals(Optional.of(LocalTime.of(8, 0)), newSunUpDownCalculator(TwilightType.Mathematical).sunUpTime(289, 2));
	}

	@Test
	final void sunUpTimeNotExists() {
		assertTrue(newSunUpDownCalculator(TwilightType.Astronomical).sunUpTime(182, 2).isEmpty());
	}

	@Test
	final void sunDownTimeNotExists() {
		assertTrue(newSunUpDownCalculator(TwilightType.Astronomical).sunDownTime(182, 2).isEmpty());
	}

	@Test
	final void sunDownTimeGt24() {
		assertEquals(Optional.of(LocalTime.of(1, 10)), newSunUpDownCalculator(TwilightType.Astronomical).sunDownTime(144, 2));
	}

	@ParameterizedTest
	@MethodSource("days")
	final void sunUpDownAstronomicalExists(final int day) {

		final var sunUpDownCalculator = newSunUpDownCalculator(TwilightType.Astronomical);
		final var offset = offsetEstimation(day);
		if ((day >= 145) && (day <= 201)) {
			assertTrue(sunUpDownCalculator.sunDownTime(day, offset).isEmpty());
			assertTrue(sunUpDownCalculator.sunUpTime(day, offset).isEmpty());
		} else {
			assertTrue(sunUpDownCalculator.sunDownTime(day, offset).isPresent());
			assertTrue(sunUpDownCalculator.sunUpTime(day, offset).isPresent());
		}

	}

	static Collection<Integer> days() {
		return IntStream.range(0, 365).mapToObj(Integer::valueOf).collect(Collectors.toList());
	}

	@ParameterizedTest
	@MethodSource("days")
	final void sunUpDownMathematicalExists(final int day) {
		final var sunUpDownCalculator = newSunUpDownCalculator(TwilightType.Mathematical);
		final var offset = offsetEstimation(day);
		final var sunDownTime = sunUpDownCalculator.sunDownTime(day, offset);
		assertFalse(sunDownTime.isEmpty());
		assertFalse(sunUpDownCalculator.sunUpTime(day, offset).isEmpty());
		assertTrue(sunDownTime.get().isAfter(LocalTime.of(16, 25)));
	}

	@ParameterizedTest
	@MethodSource("days")
	final void sunUpDownCivilExists(final int day) {
		final var offset = offsetEstimation(day);
		final var sunUpDownCalculator = newSunUpDownCalculator(TwilightType.Civil);
		final var sunDownTime = sunUpDownCalculator.sunDownTime(day, offset);
		assertFalse(sunDownTime.isEmpty());
		assertFalse(sunUpDownCalculator.sunUpTime(day, offset).isEmpty());
		assertTrue(sunDownTime.get().isAfter(LocalTime.of(17, 5)));
	}

	@ParameterizedTest
	@MethodSource("days")
	final void sunUpDownNauticalExists(final int day) {
		final var sunUpDownCalculator = newSunUpDownCalculator(TwilightType.Nautical);
		final var offset = offsetEstimation(day);
		Optional<LocalTime> sunDownTime = sunUpDownCalculator.sunDownTime(day, offset);
		assertFalse(sunDownTime.isEmpty());
		assertFalse(sunUpDownCalculator.sunUpTime(day, offset).isEmpty());
		assertTrue(sunDownTime.get().isAfter(LocalTime.of(17, 45)));
	}

	int offsetEstimation(final int day) {

		if (day <= 110) {
			return 1;
		}

		if (day >= 290) {
			return 1;
		}

		return 2;
	}

}
