package de.mq.iot2.rules.support;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.RuleService.Argument;

@Rule(name = "Timer-Rule", description = "Timer-Rule", priority = 1)
public class TimerRulesImpl {
	
	static final LocalTime MAX_SUN_UP_TIME_DEFAULT = LocalTime.of(10, 0);
	static final LocalTime MIN_SUN_UP_TIME_DEFAULT = LocalTime.of(5,0);
	static final LocalTime MAX_SUN_DOWN_TIME_DEFAULT = LocalTime.of(22, 15);
	static final LocalTime MIN_SUN_DOWN_TIME_DEFAULT = LocalTime.of(15,0);
	private static Logger LOGGER = LoggerFactory.getLogger(TimerRulesImpl.class);
	
	@Condition
	public  final boolean  evaluate() {
		return true;
	}
	@Action(order=1)
	public final void setup(final Facts facts, @Fact("Parameter")  final Map<Key,Object> parameter) {
		
		facts.put(Argument.Timer.name(), new ArrayList<LocalTime>());
		if(! parameter.containsKey(Key.MinSunDownTime)) {
			parameter.put(Key.MinSunDownTime, MIN_SUN_DOWN_TIME_DEFAULT);
		}
		if( ! parameter.containsKey(Key.MaxSunDownTime)) {
			parameter.put(Key.MaxSunDownTime, MAX_SUN_DOWN_TIME_DEFAULT);
		}
		
		if(! parameter.containsKey(Key.MinSunUpTime)) {
			parameter.put(Key.MinSunUpTime, MIN_SUN_UP_TIME_DEFAULT);
		}
		if(! parameter.containsKey(Key.MaxSunUpTime)) {
			parameter.put(Key.MaxSunUpTime, MAX_SUN_UP_TIME_DEFAULT);
		}
		
		facts.put(Argument.Parameter.name(), Collections.unmodifiableMap(parameter));
	}
	
	
	
	
	
	@Action(order=2)
	 public  final void timerUpFirst(@Fact( "Parameter" ) final Map<Key, Object> parameter,@Fact("Timer") Collection<Entry<String,LocalTime>> timerList) {
		final var timerName="T0";
		if( ! parameter.containsKey(Key.UpTime)) {
			final var message = String.format("Parameter %s is missing.", Key.UpTime);
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		
		LocalTime time = (LocalTime)  parameter.get(Key.UpTime);
		LOGGER.debug("Add Timer {} {}." ,  timerName,time);
		
		timerList.add(new AbstractMap.SimpleImmutableEntry<>(timerName , time));
	 }

}
