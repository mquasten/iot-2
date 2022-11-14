package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.junit.jupiter.api.Test;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.RuleService.Argument;

class TimerRulesImplTest {

	private final  TimerRulesImpl timerRules = new  TimerRulesImpl();
	@Test
	void evaluate() {
		assertTrue(timerRules.evaluate());
	}
	
	@Test
	void setup() {
		final var facts = new Facts();
		final Map<Key,Object> parameter =new HashMap<>();
		timerRules.setup(facts, parameter);
		
		assertTrue(facts.asMap().containsKey(Argument.Timer.name()));
		assertEquals(4, parameter.size());
		assertEquals(TimerRulesImpl.MAX_SUN_UP_TIME_DEFAULT, parameter.get(Key.MaxSunUpTime));
		assertEquals(TimerRulesImpl.MIN_SUN_UP_TIME_DEFAULT, parameter.get(Key.MinSunUpTime));
		assertEquals(TimerRulesImpl.MAX_SUN_DOWN_TIME_DEFAULT, parameter.get(Key.MaxSunDownTime));
		assertEquals(TimerRulesImpl.MIN_SUN_DOWN_TIME_DEFAULT, parameter.get(Key.MinSunDownTime));
		assertTrue(facts.asMap().containsKey(Argument.Parameter.name()));
	}
	
	@Test
	void setupDoNotOverwrite() {
		final Map<Key,Object> parameter = new HashMap<>();
		final var time = LocalTime.now();
		parameter.put(Key.MaxSunUpTime, time);
		parameter.put(Key.MinSunUpTime, time);
		parameter.put(Key.MaxSunDownTime, time);
		parameter.put(Key.MinSunDownTime, time);
	
		final var facts = new Facts();
		timerRules.setup(facts, parameter);
		
		parameter.values().forEach(value -> assertEquals(time, value));
	}
	
	@Test
	void setupParameterUnmodifiableMap() {
		final var facts = new Facts();
		timerRules.setup(facts, new HashMap<>());
		
		final Map<?,?> parameter =(Map<?, ?>) facts.asMap().get(Argument.Parameter.name());
		assertThrows(UnsupportedOperationException.class,() -> parameter.clear());
	}

}
