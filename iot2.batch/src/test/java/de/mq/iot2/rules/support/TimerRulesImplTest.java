package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimerRulesImplTest {

	private final  TimerRulesImpl timerRules = new  TimerRulesImpl();
	@Test
	void evaluate() {
		assertTrue(timerRules.evaluate());
	}
	

}
