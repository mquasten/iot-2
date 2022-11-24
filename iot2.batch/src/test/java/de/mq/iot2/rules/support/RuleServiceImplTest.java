package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.CalendarService.TimeType;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.sysvars.SystemVariable;

class RuleServiceImplTest {

	@Test
	void endOfDayRules() {
		final var cycle = Mockito.mock(Cycle.class);
		final var ruleService = new RuleServiceImpl(List.of(new OtherVariablesRulesImpl(), new TimerRuleImpl()));
		final var minSunDownTime = LocalTime.parse("17:15");
		final var upTime = LocalTime.parse("07:15");
		final var sunUpTime = LocalTime.of(8, 20);
		final Map<Key,Object> parameters = Map.of(Key.MinSunUpTime, LocalTime.parse("05:30"), Key.MaxSunUpTime, LocalTime.parse("09:30"), Key.MinSunDownTime, minSunDownTime, Key.MaxSunDownTime,
				LocalTime.parse("22:15"), Key.UpTime, upTime, Key.SunUpDownType, TwilightType.Mathematical);
		final var arguments = Map.of(EndOfDayArguments.Date, LocalDate.of(2022, 12, 25), EndOfDayArguments.TimeType, TimeType.Winter, EndOfDayArguments.SunUpTime, Optional.of(sunUpTime),
				EndOfDayArguments.SunDownTime, Optional.of(LocalTime.of(16, 45)), EndOfDayArguments.Cycle, cycle);

		 Map<String,Object> results = ruleService.process(parameters, arguments);

		assertEquals(7, results.size());
		assertTrue(results.keySet().containsAll(arguments.keySet().stream().map(EndOfDayArguments::name).collect(Collectors.toList())));
		assertTrue(results.containsKey(EndOfDayArguments.Timer.name()));

		@SuppressWarnings("unchecked")
		final var timerMap = ((Collection<Entry<String, LocalTime>>) results.get(EndOfDayArguments.Timer.name())).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(3, timerMap.size());
		assertEquals(upTime, timerMap.get("T0"));
		assertEquals(sunUpTime, timerMap.get("T1"));
		assertEquals(minSunDownTime, timerMap.get("T6"));

		assertTrue(results.containsKey(EndOfDayArguments.SystemVariables.name()));
		@SuppressWarnings("unchecked")
		final var systemVariableMap = ((Collection<SystemVariable>) results.get(EndOfDayArguments.SystemVariables.name())).stream()
				.collect(Collectors.toMap(SystemVariable::getName, SystemVariable::getValue));
		assertEquals(5, systemVariableMap.size());
		assertEquals("T0:7.15;T1:8.2;T6:17.15", systemVariableMap.get(TimerRuleImpl.DAILY_EVENTS_SYSTEM_VARIABLE_NAME));
		assertEquals("" + Month.DECEMBER.ordinal(), systemVariableMap.get(OtherVariablesRulesImpl.MONTH_SYSTEM_VARIABLE_NAME));
		assertEquals(String.valueOf(false), systemVariableMap.get(OtherVariablesRulesImpl.WORKING_DAY_SYSTEM_VARIABLE_NAME));
		assertEquals("" + TimeType.Winter.ordinal(), systemVariableMap.get(OtherVariablesRulesImpl.TIME_TYP_SYSTEM_VARIABLE_NAME));
		assertTrue(1 >= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
				- LocalDateTime.parse(systemVariableMap.get(OtherVariablesRulesImpl.LAST_BATCH_RUN_VARIABLE_NAME), DateTimeFormatter.ofPattern(OtherVariablesRulesImpl.LAST_BATCH_RUN_DATE_FORMAT))
						.toEpochSecond(ZoneOffset.UTC));

	}

	@Test
	void endOfDayRulesInjectParameters() {
		final var timerRule = new TimerRuleImpl();
		final var ruleService = new RuleServiceImpl(List.of(new OtherVariablesRulesImpl(), timerRule));
		final var minSunDownTime = LocalTime.parse("17:15");
		final var upTime = LocalTime.parse("07:15");
		final var maxSunUpTime = LocalTime.parse("09:30");
		final var minSunUpTime = LocalTime.parse("05:30");
		final var maxSunDownTime = LocalTime.parse("22:15");
		final Map<Key,Object> parameters = Map.of(Key.MinSunUpTime, minSunUpTime, Key.MaxSunUpTime, maxSunUpTime, Key.MinSunDownTime, minSunDownTime, Key.MaxSunDownTime, maxSunDownTime, Key.UpTime, upTime,
				Key.SunUpDownType, TwilightType.Mathematical);

		final var arguments = Map.of(EndOfDayArguments.Date, LocalDate.of(2022, 12, 25), EndOfDayArguments.TimeType, TimeType.Winter, EndOfDayArguments.SunUpTime, Optional.of(LocalTime.of(8, 20)),
				EndOfDayArguments.SunDownTime, Optional.of(LocalTime.of(16, 45)), EndOfDayArguments.Cycle, Mockito.mock(Cycle.class));

		ruleService.process(parameters, arguments);

		final Map<String, LocalTime> injectedValues = new HashMap<>();
		ReflectionUtils.doWithFields(TimerRuleImpl.class, field -> injectedValues.put(StringUtils.capitalize(field.getName()), (LocalTime) ReflectionTestUtils.getField(timerRule, field.getName())),
				field -> field.getType() == LocalTime.class);
		assertEquals(5, injectedValues.size());
		assertEquals(upTime, injectedValues.get(Key.UpTime.name()));
		assertEquals(maxSunUpTime, injectedValues.get(Key.MaxSunUpTime.name()));
		assertEquals(minSunDownTime, injectedValues.get(Key.MinSunDownTime.name()));
		assertEquals(minSunUpTime, injectedValues.get(Key.MinSunUpTime.name()));
		assertEquals(maxSunDownTime, injectedValues.get(Key.MaxSunDownTime.name()));
	}

	@Test
	void endOfDayRulesParametersDefaultValue() {
		final var timerRule = new TimerRuleImpl();
		final var ruleService = new RuleServiceImpl(List.of(new OtherVariablesRulesImpl(), timerRule));
		final var arguments = Map.of(EndOfDayArguments.Date, LocalDate.of(2022, 12, 25), EndOfDayArguments.TimeType, TimeType.Winter, EndOfDayArguments.SunUpTime, Optional.of(LocalTime.of(8, 20)),
				EndOfDayArguments.SunDownTime, Optional.of(LocalTime.of(16, 45)), EndOfDayArguments.Cycle, Mockito.mock(Cycle.class));

		ruleService.process(Map.of(), arguments);

		final Map<String, LocalTime> injectedValues = new HashMap<>();
		ReflectionUtils.doWithFields(TimerRuleImpl.class, field -> injectedValues.put(StringUtils.capitalize(field.getName()), (LocalTime) ReflectionTestUtils.getField(timerRule, field.getName())),
				field -> field.getType() == LocalTime.class);
		assertEquals(5, injectedValues.size());
		assertNull(injectedValues.get(Key.UpTime.name()));
		assertEquals(LocalTime.of(10, 0), injectedValues.get(Key.MaxSunUpTime.name()));
		assertEquals(LocalTime.of(15, 0), injectedValues.get(Key.MinSunDownTime.name()));
		assertEquals(LocalTime.of(5, 0), injectedValues.get(Key.MinSunUpTime.name()));
		assertEquals(LocalTime.of(23, 0), injectedValues.get(Key.MaxSunDownTime.name()));
	}

}
