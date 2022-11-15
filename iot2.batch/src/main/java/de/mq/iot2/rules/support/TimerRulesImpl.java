package de.mq.iot2.rules.support;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Map.Entry;
import java.util.Optional;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.EndOfDayArguments;

@Rule(name = "Timer-Rule", description = "Timer-Rule", priority = 1)
public class TimerRulesImpl {

	private static Logger LOGGER = LoggerFactory.getLogger(TimerRulesImpl.class);
	@ParameterValue(Key.MaxSunUpTime)
	private final LocalTime maxSunUpTime = LocalTime.of(10, 0);
	@ParameterValue(Key.MinSunUpTime)
	private final LocalTime minSunUpTime = LocalTime.of(5, 0);
	@ParameterValue(Key.MaxSunDownTime)
	private final LocalTime maxSunDownTime = LocalTime.of(23, 00);
	@ParameterValue(Key.MinSunDownTime)
	private final LocalTime minSunDownTime = LocalTime.of(15, 0);
	@ParameterValue(Key.UpTime)
	private final LocalTime upTime = null;

	@Condition
	public final boolean evaluate() {
		return true;
	}

	@Action(order = 1)
	public final void setup(final Facts facts) {
		facts.put(EndOfDayArguments.Timer.name(), new ArrayList<LocalTime>());
	}

	@Action(order = 2)
	public final void timerUpFirst(@Fact("Timer") Collection<Entry<String, LocalTime>> timerList) {
		final var timerName = "T0";
		if (upTime == null) {
			final var message = String.format("Parameter %s is missing.", Key.UpTime);
			LOGGER.warn(message);
			return;
		}

		LOGGER.debug("Add Timer {} {}.", timerName, upTime);

		timerList.add(new AbstractMap.SimpleImmutableEntry<>(timerName, upTime));
	}

	@Action(order = 2)
	public final void timerUpSecond(@Fact("SunUpTime") final Optional<LocalTime> sunUpTime, @Fact("Timer") Collection<Entry<String, LocalTime>> timerList) {
		final var timerName = "T1";
		final var time = sunUpTime(sunUpTime);
		LOGGER.debug("Add Timer {} {}.", timerName, time);
		timerList.add(new AbstractMap.SimpleImmutableEntry<>(timerName, time));
	}

	private LocalTime sunUpTime(final Optional<LocalTime> sunUpTime) {
		final var time = sunUpTime.orElse(minSunUpTime);
		if (time.isBefore(minSunUpTime)) {
			return minSunUpTime;
		}
		if (time.isAfter(maxSunUpTime)) {
			return maxSunUpTime;
		}
		return time;
	}

	@Action(order = 2)
	public final void timerDown(@Fact("SunDownTime") final Optional<LocalTime> sunDownTime, @Fact("Timer") Collection<Entry<String, LocalTime>> timerList) {
		final var timerName = "T6";
		final var time = sunDownTime(sunDownTime);
		LOGGER.debug("Add Timer {} {}.", timerName, time);
		timerList.add(new AbstractMap.SimpleImmutableEntry<>(timerName, time));
	}

	private LocalTime sunDownTime(final Optional<LocalTime> sunDownTime) {
		final var time = sunDownTime.orElse(maxSunDownTime);
		if (time.isBefore(minSunDownTime)) {
			return minSunDownTime;
		}
		if (time.isAfter(maxSunDownTime)) {
			return maxSunDownTime;
		}
		return time;
	}

}
