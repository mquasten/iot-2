package de.mq.iot2.rules.support;

import java.util.HashMap;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.stereotype.Service;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.RuleService;

@Service
class RuleServiceImpl implements RuleService {
	
	private final EndOfDayRulesRepository endOfDayRulesRepository;
	
	RuleServiceImpl(final EndOfDayRulesRepository endOfDayRulesRepository){
		this.endOfDayRulesRepository=endOfDayRulesRepository;
	}
	
	
	@Override
	final public void processEndOfDayRulesEngine(final Map<Key, ? extends Object> parameter, final Map<Argument,  ? extends Object> arguments ) {
		final RulesEngine rulesEngine = endOfDayRulesRepository.rulesEngine();
		rulesEngine.fire(endOfDayRulesRepository.rules(), facts(parameter, arguments));
		
		
	}

	private Facts facts(final Map<Key, ? extends Object> parameter,final Map<Argument, ? extends Object> arguments) {
		final Facts facts = new Facts();
		arguments.entrySet().forEach(entry -> facts.put(entry.getKey().name(), entry.getValue()));
		facts.put(Argument.Parameter.name(), new HashMap<Key,Object>(parameter));
		return facts;
	}
	
	
}
