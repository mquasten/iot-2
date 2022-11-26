package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.jeasy.rules.api.Facts;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.sysvars.SystemVariable;

class TimerRuleImplTest {

	private final TimerRuleImpl timerRules = new TimerRuleImpl();

	@Test
	void evaluate() {
		assertTrue(timerRules.evaluate());
	}

	@Test
	void setup() {
		final Facts facts = new Facts();

		timerRules.setup(facts);

		assertNotNull(facts.getFact(EndOfDayArguments.Timer.name()));
		assertEquals(0, ((Collection<?>) facts.get(EndOfDayArguments.Timer.name())).size());

		assertNotNull(facts.getFact(EndOfDayArguments.SystemVariables.name()));
		assertEquals(0, ((Collection<?>) facts.get(EndOfDayArguments.SystemVariables.name())).size());
	}

	@Test
	void timerUpFirst() {

		final var upTime = LocalTime.now();
		injectParameter(Key.UpTime, upTime);
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();

		timerRules.timerUpFirst(timerList);

		assertEquals(1, timerList.size());
		assertEquals("T0", timerList.get(0).getKey());
		assertEquals(upTime, timerList.get(0).getValue());
	}

	@Test
	void timerUpFirstUpTimeMissing() {

		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();

		timerRules.timerUpFirst(timerList);

		assertEquals(0, timerList.size());
	}

	@Test
	void timerUpSecond() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		final var sunUpTime = LocalTime.of(7, 7);
		timerRules.timerUpSecond(Optional.of(sunUpTime), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T1", timerList.get(0).getKey());
		assertEquals(sunUpTime, timerList.get(0).getValue());
	}

	@Test
	void timerUpSecondSunUpBeforeLimit() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		timerRules.timerUpSecond(Optional.of(LocalTime.of(3, 0)), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T1", timerList.get(0).getKey());
		assertEquals(getParameter(Key.MinSunUpTime), timerList.get(0).getValue());
	}

	@Test
	void timerUpSecondSunUpAfterLimit() {
		final var maxSunUpTime = LocalTime.of(8, 0);
		injectParameter(Key.MaxSunUpTime, maxSunUpTime);
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		timerRules.timerUpSecond(Optional.of(LocalTime.of(8, 30)), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T1", timerList.get(0).getKey());
		assertEquals(maxSunUpTime, timerList.get(0).getValue());
	}

	@Test
	void timerUpSecondMissingSunUpTime() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();

		timerRules.timerUpSecond(Optional.empty(), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T1", timerList.get(0).getKey());
		assertEquals(getParameter(Key.MinSunUpTime), timerList.get(0).getValue());
	}
	
	@Test
	void timerShadowTemperature() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		final var temperature = 25d;
		final var time = LocalTime.of(9, 0);
		injectParameter(Key.ShadowTemperature, temperature);
		injectParameter(Key.ShadowTime, time);
		
		timerRules.timerShadowTemperature(Optional.of(temperature), timerList);
		
		assertEquals(1, timerList.size());
		assertEquals("T2", timerList.get(0).getKey());
		assertEquals(time, timerList.get(0).getValue());
		
	}
	
	@Test
	void timerShadowTemperatureTemperatorLessThanLimit() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		final var time = LocalTime.of(9, 0);
		injectParameter(Key.ShadowTemperature, 25d);
		injectParameter(Key.ShadowTime, time);
		
		timerRules.timerShadowTemperature(Optional.of(24d), timerList);
		
		assertEquals(0, timerList.size());
	}
	
	@Test
	void timerShadowTemperatureNoForeCast() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		final var temperature = 25d;
		final var time = LocalTime.of(9, 0);
		injectParameter(Key.ShadowTemperature, temperature);
		injectParameter(Key.ShadowTime, time);
		
		timerRules.timerShadowTemperature(Optional.empty(), timerList);
		
		assertEquals(0, timerList.size());
	}
	
	@Test
	void timerShadowTemperatureShadowTimeNull() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		final var temperature = 25d;
		
		injectParameter(Key.ShadowTemperature, temperature);
		
		
		timerRules.timerShadowTemperature(Optional.of(temperature), timerList);
		
		assertEquals(0, timerList.size());
	}

	@Test
	void timerDown() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		final var sunDownTime = LocalTime.of(20, 0);
		timerRules.timerDown(Optional.of(sunDownTime), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T6", timerList.get(0).getKey());
		assertEquals(sunDownTime, timerList.get(0).getValue());
	}

	@Test
	void timerDownSunDownBeforeLimit() {
		injectParameter(Key.MinSunDownTime, LocalTime.of(17, 15));
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		timerRules.timerDown(Optional.of(LocalTime.of(16, 45)), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T6", timerList.get(0).getKey());
		assertEquals(getParameter(Key.MinSunDownTime), timerList.get(0).getValue());
	}

	@Test
	void timerDownSunDownAfterLimit() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		timerRules.timerDown(Optional.of(LocalTime.of(23, 15)), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T6", timerList.get(0).getKey());
		assertEquals(getParameter(Key.MaxSunDownTime), timerList.get(0).getValue());
	}

	@Test
	void timerDownMissingSunDown() {
		final List<Entry<String, LocalTime>> timerList = new ArrayList<>();
		timerRules.timerDown(Optional.empty(), timerList);

		assertEquals(1, timerList.size());
		assertEquals("T6", timerList.get(0).getKey());
		assertEquals(getParameter(Key.MaxSunDownTime), timerList.get(0).getValue());
	}

	private void injectParameter(final Key key, final Object time) {
		ReflectionUtils.doWithFields(TimerRuleImpl.class, field -> ReflectionTestUtils.setField(timerRules, field.getName(), time),
				field -> field.isAnnotationPresent(ParameterValue.class) && field.getDeclaredAnnotation(ParameterValue.class).value() == key);
	}

	private LocalTime getParameter(final Key key) {
		final LocalTime time[] = new LocalTime[] { null };
		ReflectionUtils.doWithFields(TimerRuleImpl.class, field -> time[0] = (LocalTime) ReflectionTestUtils.getField(timerRules, field.getName()),
				field -> field.isAnnotationPresent(ParameterValue.class) && field.getDeclaredAnnotation(ParameterValue.class).value() == key);
		return time[0];
	}

	@Test
	void addSystemVariable() {

		final Collection<Entry<String, LocalTime>> timer = List.of(new SimpleImmutableEntry<String, LocalTime>("T6", LocalTime.of(17,15)), new SimpleImmutableEntry<String, LocalTime>("T0", LocalTime.of(7, 15)), new SimpleImmutableEntry<String, LocalTime>("T1", LocalTime.of(8, 5)));
		final Collection<SystemVariable> systemVariables = new ArrayList<>();
		
		timerRules.addSystemVariable(timer, systemVariables);
		
		assertEquals(1, systemVariables.size());
		assertEquals(TimerRuleImpl.DAILY_EVENTS_SYSTEM_VARIABLE_NAME, systemVariables.iterator().next().getName());
		assertEquals("T0:7.15;T1:8.05;T6:17.15", systemVariables.iterator().next().getValue());
	}

}
