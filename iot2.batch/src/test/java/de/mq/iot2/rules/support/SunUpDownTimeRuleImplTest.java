package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.Optional;
import java.util.stream.Stream;

import org.jeasy.rules.api.Facts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.rules.EndOfDayArguments;

class SunUpDownTimeRuleImplTest {

	private final CalendarService calendarService = Mockito.mock(CalendarService.class);
	private final Facts facts = new Facts();

	private final Optional<LocalTime> sunUpTime = Optional.of(LocalTime.of(6, 0));
	private final Optional<LocalTime> sunDownTime = Optional.of(LocalTime.of(21, 0));

	private final SunUpDownTimeRuleImpl sunUpDownTimeRule = new SunUpDownTimeRuleImpl(calendarService);

	@Test
	void evaluate() {
		assertTrue(sunUpDownTimeRule.evaluate());
	}

	@Test
	void overwriteSunUpSunDownTwilightTypeAware() {
		final var date = LocalDate.now();
		final var twilightType = TwilightType.Nautical;
		Mockito.when(calendarService.sunUpTime(date, twilightType)).thenReturn(sunUpTime);
		Mockito.when(calendarService.sunDownTime(date, twilightType)).thenReturn(sunDownTime);

		sunUpDownTimeRule.overwriteSunUpSunDown(date, Optional.of(twilightType), facts);

		Mockito.verify(calendarService).sunUpTime(date, twilightType);
		Mockito.verify(calendarService).sunDownTime(date, twilightType);
		assertEquals(2, facts.asMap().size());
		assertEquals(sunDownTime, facts.get(EndOfDayArguments.SunDownTime.name()));
		assertEquals(sunUpTime, facts.get(EndOfDayArguments.SunUpTime.name()));
	}

	@ParameterizedTest()
	@MethodSource("winterTimeDates")
	void overwriteSunUpSunDownTwilightTypeMissingWinterTime(final LocalDate date) {
		final var facts = new Facts();
		final var sunUpTime = Optional.of(LocalTime.of(6, 0));
		final var sunDownTime = Optional.of(LocalTime.of(21, 0));

		Mockito.when(calendarService.sunUpTime(date, TwilightType.Civil)).thenReturn(sunUpTime);
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Civil)).thenReturn(sunDownTime);

		sunUpDownTimeRule.overwriteSunUpSunDown(date, Optional.empty(), facts);

		Mockito.verify(calendarService).sunUpTime(date, TwilightType.Civil);
		Mockito.verify(calendarService).sunDownTime(date, TwilightType.Civil);
		assertEquals(2, facts.asMap().size());
		assertEquals(sunDownTime, facts.get(EndOfDayArguments.SunDownTime.name()));
		assertEquals(sunUpTime, facts.get(EndOfDayArguments.SunUpTime.name()));
	}

	private static Stream<LocalDate> winterTimeDates() {
		return Stream.of(LocalDate.of(Year.now().getValue(), Month.MARCH.getValue(), 2), LocalDate.of(Year.now().getValue(), Month.DECEMBER.getValue(), 21), LocalDate.of(Year.now().getValue(), Month.OCTOBER.getValue(), 31));
	}
	
	
	@ParameterizedTest()
	@MethodSource("summerTimeDates")
	void overwriteSunUpSunDownTwilightTypeMissingSummerTime(final LocalDate date) {
		final var facts = new Facts();
		final var sunUpTime = Optional.of(LocalTime.of(6, 0));
		final var sunDownTime = Optional.of(LocalTime.of(21, 0));

		Mockito.when(calendarService.sunUpTime(date, TwilightType.Mathematical)).thenReturn(sunUpTime);
		Mockito.when(calendarService.sunDownTime(date, TwilightType.Mathematical)).thenReturn(sunDownTime);

		sunUpDownTimeRule.overwriteSunUpSunDown(date, Optional.empty(), facts);

		Mockito.verify(calendarService).sunUpTime(date, TwilightType.Mathematical);
		Mockito.verify(calendarService).sunDownTime(date, TwilightType.Mathematical);
		assertEquals(2, facts.asMap().size());
		assertEquals(sunDownTime, facts.get(EndOfDayArguments.SunDownTime.name()));
		assertEquals(sunUpTime, facts.get(EndOfDayArguments.SunUpTime.name()));
	}
	
	private static Stream<LocalDate> summerTimeDates() {
		return Stream.of(LocalDate.of(Year.now().getValue(), Month.MARCH.getValue(), 31), LocalDate.of(Year.now().getValue(), Month.JUNE.getValue(), 21), LocalDate.of(Year.now().getValue(), Month.OCTOBER.getValue(), 1));
	}
	
	
}
