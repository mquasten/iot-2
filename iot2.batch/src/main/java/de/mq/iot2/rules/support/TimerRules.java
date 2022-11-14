package de.mq.iot2.rules.support;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
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
public class TimerRules {
	
	private static Logger LOGGER = LoggerFactory.getLogger(TimerRules.class);
	
	@Condition
	public  final boolean  evaluate(final Facts facts) {
		return true;
	}
	@Action(order=1)
	public final void setup(Facts facts ) {
		facts.put(Argument.Timer.name(), new ArrayList<>());
	}
	
	
	
	@Action(order=2)
	 public  final void timerUpTimeFirst(@Fact( "Parameter" ) final Map<Key, Object> parameter,@Fact("Timer") Collection<Entry<String,LocalTime>> timerList) {
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
