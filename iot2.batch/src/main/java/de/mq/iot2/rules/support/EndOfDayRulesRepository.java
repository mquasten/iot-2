package de.mq.iot2.rules.support;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;


@Component
interface EndOfDayRulesRepository {
	@Lookup("EndOfDayRules")
	Rules rules();
	
	@Lookup
	public RulesEngine rulesEngine();


}
