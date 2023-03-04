package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.sysvars.SystemVariable;

class OtherVariablesRulesImplTest {
	private final OtherVariablesRulesImpl otherVariablesRules = new OtherVariablesRulesImpl();
	final Collection<SystemVariable> systemVariables = new ArrayList<>();

	@Test
	void evaluate() {
		assertTrue(otherVariablesRules.evaluate());
	}

	@Test
	void workingdayTrue() {
		final Cycle cycle = Mockito.mock(Cycle.class);
		Mockito.when(cycle.isDeaultCycle()).thenReturn(true);

		otherVariablesRules.workingday(cycle, systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.WORKING_DAY_SYSTEM_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertEquals("" + true, systemVariables.stream().findAny().get().getValue());
	}

	@Test
	void workingdayFalse() {
		otherVariablesRules.workingday(Mockito.mock(Cycle.class), systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals("" + false, systemVariables.stream().findAny().get().getValue());
	}

	@Test
	void workingdayNullValues() {
		assertThrows(IllegalArgumentException.class, () -> otherVariablesRules.workingday(null, new ArrayList<>()));
		assertThrows(IllegalArgumentException.class, () -> otherVariablesRules.workingday(Mockito.mock(Cycle.class), null));
	}

	@Test
	void timerTypeWinter() {
		otherVariablesRules.timeType(LocalDate.of(2023, 3, 2), systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.TIME_TYP_SYSTEM_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertEquals(String.valueOf(0), systemVariables.stream().findAny().get().getValue());
	}

	@Test
	void timerTypeSummer() {
		otherVariablesRules.timeType(LocalDate.of(2023, 3, 31), systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.TIME_TYP_SYSTEM_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertEquals(String.valueOf(1), systemVariables.stream().findAny().get().getValue());
	}

	@Test
	void timeTypeNullValues() {
		assertThrows(IllegalArgumentException.class, () -> otherVariablesRules.timeType(null, new ArrayList<>()));
		assertThrows(IllegalArgumentException.class, () -> otherVariablesRules.timeType(LocalDate.now(), null));
	}

	@ParameterizedTest
	@EnumSource(Month.class)
	void month(final Month month) {
		otherVariablesRules.month(LocalDate.of(2022, month, 11), systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.MONTH_SYSTEM_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertEquals(String.valueOf(month.ordinal()), systemVariables.stream().findAny().get().getValue());
	}

	@Test
	void MonthNullValues() {
		assertThrows(IllegalArgumentException.class, () -> otherVariablesRules.month(null, new ArrayList<>()));
		assertThrows(IllegalArgumentException.class, () -> otherVariablesRules.month(LocalDate.now(), null));
	}

	@Test
	void maxTemperature() {
		final var temperature = 11.11d;
		otherVariablesRules.maxTemperature(Optional.of(temperature), systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.TEMPERATURE_SYSTEM_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertEquals(OtherVariablesRulesImpl.DECIMAL_FORMAT_CCU2.format(temperature), systemVariables.stream().findAny().get().getValue());
	}

	@Test
	void maxTemperatureNoForecast() {
		otherVariablesRules.maxTemperature(Optional.empty(), systemVariables);

		assertEquals(0, systemVariables.size());

	}

	@Test
	void lastBatchrun() {
		otherVariablesRules.lastBatchrun(systemVariables);

		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.LAST_BATCH_RUN_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertTrue(1 >= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
				- LocalDateTime.parse(systemVariables.stream().findAny().get().getValue(), DateTimeFormatter.ofPattern(OtherVariablesRulesImpl.LAST_BATCH_RUN_DATE_FORMAT))
						.toEpochSecond(ZoneOffset.UTC));
	}

}
