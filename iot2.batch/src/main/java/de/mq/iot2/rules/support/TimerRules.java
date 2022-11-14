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

@Rule(name = "Timer-Rule", description = "Timer-Rule", priority = 1)
public class TimerRules {
	private static Logger LOGGER = LoggerFactory.getLogger(TimerRules.class);
	@Condition
	public  final boolean  evaluate(final Facts facts) {
		return true;
	}
	@Action(order=1)
	public final void setup(Facts facts ) {
		facts.put("timer", new ArrayList<>());
		System.out.println("Regel order 1 wird ausgeführt.");
	}
	
	@Action(order=2)
	 public  final void timerUpTimeFirst(@Fact("parameter") final Map<Key, Object> parameter,@Fact("timer") Collection<Entry<String,LocalTime>> timer) {
		if( ! parameter.containsKey(Key.UpTime)) {
			final var message = String.format("Parameter %s is missing.", Key.UpTime);
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		
		
		timer.add(new AbstractMap.SimpleImmutableEntry<>("T0" , (LocalTime)  parameter.get(Key.UpTime)));
	 }

}
