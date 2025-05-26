package de.mq.iot2.rules.support;

import java.time.LocalDate;
import java.util.Optional;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.support.ZoneUtil;
import de.mq.iot2.rules.EndOfDayArguments;

@Rule(name = "SunUpDownTime-Rule", description = "SunUpDownTime-Rule", priority = SunUpDownTimeRuleImpl.PRIORTY)
public class SunUpDownTimeRuleImpl {

	private final CalendarService calendarService;

	static final boolean OVERWRITE_SUN_UP_DOWN_TIMES = true;
	static final  int PRIORTY = Integer.MIN_VALUE;

	SunUpDownTimeRuleImpl(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	@Condition
	public final boolean evaluate() {
		return true;
	}

	@Action()
	public final void overwriteSunUpSunDown(@Fact("Date") final LocalDate date, @Fact("TwilightType") final Optional<TwilightType> twilightTypeFromConfiguration, final Facts facts) {
		final var twilightType = twilightTypeFromConfiguration.orElse(ZoneUtil.isEuropeanSummertime(date) ? TwilightType.Mathematical : TwilightType.Civil);
		final var sunUpTime = calendarService.sunUpTime(date, twilightType);
		final var sunDownTime = calendarService.sunDownTime(date, twilightType);
		facts.put(EndOfDayArguments.SunUpTime.name(), sunUpTime);
		facts.put(EndOfDayArguments.SunDownTime.name(), sunDownTime);
	}
}
