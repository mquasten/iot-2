package de.mq.iot2.rules.support;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mq.iot2.rules.RuleService;

@Configuration
class RulesConfiguration {

	@Bean(name = "EndOfDayRulesService")
	RuleService endOfDayRuleService() {
		return new RuleServiceImpl(endOfDayRules());
	}

	private Collection<?> endOfDayRules() {
		return Arrays.asList(new TimerRuleImpl(), new OtherVariablesRulesImpl());
	}

}
