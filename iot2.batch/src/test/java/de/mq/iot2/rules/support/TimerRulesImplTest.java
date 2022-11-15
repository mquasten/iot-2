package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.iot2.configuration.Parameter.Key;

class TimerRulesImplTest {

	private final TimerRulesImpl timerRules = new TimerRulesImpl();

	@Test
	void evaluate() {
		assertTrue(timerRules.evaluate());
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

	private void injectParameter(final Key key, final LocalTime time) {
		ReflectionUtils.doWithFields(TimerRulesImpl.class, field -> ReflectionTestUtils.setField(timerRules, field.getName(), time),
				field -> field.isAnnotationPresent(ParameterValue.class) && field.getDeclaredAnnotation(ParameterValue.class).value() == key);
	}

	private LocalTime getParameter(final Key key) {
		final LocalTime time[] = new LocalTime[] { null };
		ReflectionUtils.doWithFields(TimerRulesImpl.class, field -> time[0] = (LocalTime) ReflectionTestUtils.getField(timerRules, field.getName()),
				field -> field.isAnnotationPresent(ParameterValue.class) && field.getDeclaredAnnotation(ParameterValue.class).value() == key);
		return time[0];
	}

}
