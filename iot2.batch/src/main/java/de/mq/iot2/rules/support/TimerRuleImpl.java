package de.mq.iot2.rules.support;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.sysvars.SystemVariable;

@Rule(name = "Timer-Rule", description = "Timer-Rule", priority = Integer.MIN_VALUE)
public class TimerRuleImpl {

	private final static Logger LOGGER = LoggerFactory.getLogger(TimerRuleImpl.class);
	static final String DAILY_EVENTS_SYSTEM_VARIABLE_NAME = "DailyEvents";

	static final String EVENT_EXECUTION = "EventExecutions";
	static final String TIMER_EVENTS_SYSTEM_VARIABLE_NAME = "TimerEvents";

	private static final int DEFAULT_PRIORITY = 2;

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
	@ParameterValue(Key.ShadowTime)
	private final LocalTime shadowTime = null;
	@ParameterValue(Key.ShadowTemperature)
	private final Double shadowTemperature = Double.MAX_VALUE;

	@Condition
	public final boolean evaluate() {
		return true;
	}

	@Action(order = Integer.MIN_VALUE)
	public final void setup(final Facts facts) {
		facts.put(EndOfDayArguments.Timer.name(), new ArrayList<>());
		facts.put(EndOfDayArguments.SystemVariables.name(), new ArrayList<>());
	}

	@Action(order = DEFAULT_PRIORITY)
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

	@Action(order = DEFAULT_PRIORITY)
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

	@Action(order = DEFAULT_PRIORITY)
	public final void timerShadowTemperature(@Fact("MaxForecastTemperature") final Optional<Double> maxForecastTemperature,
			@Fact("Timer") Collection<Entry<String, LocalTime>> timerList) {
		if (shadowTime == null) {
			return;
		}
		if (maxForecastTemperature.isEmpty()) {
			return;
		}
		final var timerName = "T2";
		if (maxForecastTemperature.get() >= shadowTemperature) {
			LOGGER.debug("Add Timer {} {}.", timerName, shadowTime);
			timerList.add(new AbstractMap.SimpleImmutableEntry<>(timerName, shadowTime));
		}

	}

	@Action(order = DEFAULT_PRIORITY)
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

	@Action(order = DEFAULT_PRIORITY)
	public final void resetTimerEvents(@Fact("SystemVariables") final Collection<SystemVariable> systemVariables) {
		systemVariables.add(new SystemVariable(TIMER_EVENTS_SYSTEM_VARIABLE_NAME, String.valueOf(BigInteger.ZERO.intValue())));
	}

	@Action(order = Integer.MAX_VALUE)
	public final void addSystemVariable(@Fact("Timer") Collection<Entry<String, LocalTime>> timerList, @Fact("UpdateTime") final Optional<LocalTime> updateTime,
			@Fact("SystemVariables") final Collection<SystemVariable> systemVariables) {
		final var variableName = updateTime.isEmpty() ? DAILY_EVENTS_SYSTEM_VARIABLE_NAME : EVENT_EXECUTION;
		final var minTime = updateTime.orElse(LocalTime.of(0, 0));
		final var stringBuilder = new StringBuilder();
		final var orderedTimers = timerList.stream().filter(entry -> entry.getValue().isAfter(minTime)).sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.collect(Collectors.toList());

		IntStream.range(0, orderedTimers.size()).forEach(i -> stringBuilder.append(String.format("%s:%s%s", orderedTimers.get(i).getKey(),
				orderedTimers.get(i).getValue().getHour() + 0.01 * orderedTimers.get(i).getValue().getMinute(), i < orderedTimers.size() - 1 ? ";" : "")));
		systemVariables.add(new SystemVariable(variableName, stringBuilder.toString()));

		LOGGER.debug("Add {} Timer to SystemVariable {} value='{}'.", timerList.size(), variableName, stringBuilder);
	}

}
