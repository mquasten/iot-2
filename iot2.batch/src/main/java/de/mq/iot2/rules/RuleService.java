package de.mq.iot2.rules;

import java.util.Map;

import de.mq.iot2.configuration.Parameter.Key;



public interface RuleService {
	
	public enum Argument {
		Parameter,
		Timer,
		TimeType, 
		SunUpTime, 
		Cycle,
		SunDownTime;
		
	}

	public void processEndOfDayRulesEngine(final Map<Key, ? extends Object> parameter, final Map<Argument, ? extends Object> arguments);

}