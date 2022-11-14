package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TimeType;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.RuleService;
import de.mq.iot2.rules.RuleService.Argument;
import de.mq.iot2.rules.support.TimerRules;

@Service
public class EndOfDayBatchImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(TimerRules.class);
	private final CalendarService calendarService;

	private final ConfigurationService configurationService;
	
	private final RuleService ruleService; 

	EndOfDayBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService, final RuleService ruleService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
		this.ruleService=ruleService;
	}

	@BatchMethod(value = "end-of-day", converterClass = EndOfDayBatchArgumentConverterImpl.class)
	final void execute(final LocalDate date) {

		final Cycle cycle = calendarService.cycle(date);

		final var parameters = configurationService.parameters(RuleKey.EndOfDay, cycle);
		
		final TimeType timeType = calendarService.timeType(date);

		final var twilightType = parameters.containsKey(Key.SunUpDownType) ? (TwilightType) parameters.get(Key.SunUpDownType) : TwilightType.Mathematical;

		final var sunUpTime = calendarService.sunUpTime(date, twilightType);

		final var sunDownTime = calendarService.sunDownTime(date, twilightType);
		
		final var arguments = Map.of( Argument.Parameter,parameters ,Argument.TimeType, timeType, Argument.SunUpTime, sunUpTime, Argument.SunDownTime, sunDownTime, Argument.Cycle, cycle);

		LOGGER.debug("Start RulesEngine arguments {}.", arguments );
		ruleService.processEndOfDayRulesEngine(arguments);

	}

	

}
