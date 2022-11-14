package de.mq.iot2.rules.support;

import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import de.mq.iot2.rules.RuleService;

@Service
public abstract class AbstractRuleService implements RuleService {
	
	@Override
	final public void processEndOfDayRulesEngine(final Map<Argument,  ? extends Object> arguments ) {
		final RulesEngine rulesEngine = rulesEngiene();
		rulesEngine.fire(endOfDayRules(), facts(arguments));
		
		
	}

	private Facts facts(final Map<Argument, ? extends Object> arguments) {
		final Facts facts = new Facts();
		arguments.entrySet().forEach(entry -> facts.put(entry.getKey().name(), entry.getValue()));
		return facts;
	}
	
	@Lookup
	abstract RulesEngine rulesEngiene();
	
	@Lookup("EndOfDayRules")
	abstract Rules endOfDayRules();

}
