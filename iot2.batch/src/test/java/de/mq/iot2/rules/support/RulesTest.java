package de.mq.iot2.rules.support;

import java.time.LocalTime;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.junit.jupiter.api.Test;

import de.mq.iot2.configuration.Parameter.Key;

class RulesTest {
	
	@Test
	void testIt() {
		final DefaultRulesEngine rulesEngine = new DefaultRulesEngine(new RulesEngineParameters(false, true, false, Integer.MAX_VALUE));
		final Rules rules = new Rules(new TimerRules());
		Facts facts = new Facts();
		facts.put("parameter", Map.of(Key.UpTime, LocalTime.of(5, 30)));
	
		rulesEngine.registerRuleListener(new SimpleRulesEngineListener());
		rulesEngine.fire(rules, facts);
		
		System.out.println(facts);
	}

}
