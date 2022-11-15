package de.mq.iot2.rules;

import java.util.Map;

import org.jeasy.rules.api.Facts;

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

	public Facts process(final Map<Key, ? extends Object> parameter, final Map<? extends Enum<?>, Object> arguments);

}