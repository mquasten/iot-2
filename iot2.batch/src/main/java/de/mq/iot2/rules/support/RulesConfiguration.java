package de.mq.iot2.rules.support;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
class RulesConfiguration {
	
	@Bean()
	@Scope("prototype")
	RulesEngine rulesEngine() {
		final DefaultRulesEngine rulesEngine = new DefaultRulesEngine(new RulesEngineParameters(false, true, false, Integer.MAX_VALUE));
		rulesEngine.registerRuleListener(new SimpleRulesEngineListener());
		return rulesEngine;
	}
	@Bean(name="EndOfDayRules")
	@Scope("prototype")
	Rules endOfDayRules() {
		return new Rules(new TimerRules());
	}
	
	@Bean()
	@Scope("prototype")
	Rules other() {
		return new Rules(new TimerRules());
	}

}
