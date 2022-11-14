package de.mq.iot2.rules;

import java.util.Map;

public interface RuleService {
	
	public enum Argument {
		Parameter,
		Timer,
		TimeType, 
		SunUpTime, 
		Cycle,
		SunDownTime;
		
	}

	public void processEndOfDayRulesEngine(final Map<Argument, ? extends Object> arguments);

}