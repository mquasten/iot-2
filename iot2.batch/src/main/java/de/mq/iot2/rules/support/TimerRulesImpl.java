package de.mq.iot2.rules.support;


import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;

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
	@ParameterValue(Key.MaxSunUpTime)
	private final LocalTime maxSunUpTime=  LocalTime.of(10, 0);
	@ParameterValue(Key.MinSunUpTime)
	private final LocalTime minSunUpTime= LocalTime.of(5,0);
	@ParameterValue(Key.MaxSunDownTime)
	private final LocalTime maxSunDownTime= LocalTime.of(22, 15);
	@ParameterValue(Key.MinSunDownTime)
	private final LocalTime minSunDownTime= LocalTime.of(15,0);
	@ParameterValue(Key.UpTime)
	private final LocalTime upTime= null;
	
	@Condition
	public  final boolean  evaluate() {
		return true;
	}
	@Action(order=1)
	public final void setup(final Facts facts) {
		facts.put(Argument.Timer.name(), new ArrayList<LocalTime>());
	}
	
	
	
	
	
	@Action(order=2)
	 public  final void timerUpFirst(@Fact("Timer") Collection<Entry<String,LocalTime>> timerList) {
		final var timerName="T0";
		if( upTime==null) {
			final var message = String.format("Parameter %s is missing.", Key.UpTime);
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		
		
		LOGGER.debug("Add Timer {} {}." ,  timerName, upTime);
		
		timerList.add(new AbstractMap.SimpleImmutableEntry<>(timerName , upTime));
	 }

}
