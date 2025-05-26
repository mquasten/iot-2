package de.mq.iot2.rules.support;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.rules.RuleService;

@Configuration
class RulesConfiguration {

	@Bean(name = "EndOfDayRulesService")
	RuleService endOfDayRuleService(final CalendarService calendarService) {
		return new RuleServiceImpl(endOfDayRules(calendarService));
	}

	private Collection<?> endOfDayRules(final CalendarService calendarService ) {
		return Arrays.asList(new TimerRuleImpl(), new OtherVariablesRulesImpl(), new SunUpDownTimeRuleImpl(calendarService));
	}

}
