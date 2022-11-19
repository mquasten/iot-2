package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TimeType;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.rules.RuleService;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;

@Service
public class EndOfDayBatchImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(EndOfDayBatchImpl.class);
	private final CalendarService calendarService; 

	private final ConfigurationService configurationService;
	
	private final RuleService ruleService; 
	
	private final SystemVariableService systemVariableService;

	EndOfDayBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService, final RuleService ruleService, final SystemVariableService systemVariableService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
		this.ruleService=ruleService;
		this.systemVariableService=systemVariableService;
	}

	@BatchMethod(value = "end-of-day", converterClass = EndOfDayBatchArgumentConverterImpl.class)
	final void execute(final LocalDate date) {

		final Cycle cycle = calendarService.cycle(date);

		final var parameters = configurationService.parameters(RuleKey.EndOfDay, cycle);
		
		final TimeType timeType = calendarService.timeType(date);

		final var twilightType = parameters.containsKey(Key.SunUpDownType) ? (TwilightType) parameters.get(Key.SunUpDownType) : TwilightType.Mathematical;

		final var sunUpTime = calendarService.sunUpTime(date, twilightType);

		final var sunDownTime = calendarService.sunDownTime(date, twilightType);
		
		final var arguments = Map.of(EndOfDayArguments.Date, date,EndOfDayArguments.TimeType, timeType, EndOfDayArguments.SunUpTime, sunUpTime, EndOfDayArguments.SunDownTime, sunDownTime, EndOfDayArguments.Cycle, cycle);

		LOGGER.debug("Start RulesEngine parameters {} arguments {}.", parameters, arguments );
		final var results =  ruleService.process(parameters, arguments);
		
		Assert.notNull(results.containsKey(EndOfDayArguments.SystemVariables.name()), "Systemvariables required.");

		@SuppressWarnings("unchecked")
		final Collection<SystemVariable> systemVariables = (Collection<SystemVariable>) results.get(EndOfDayArguments.SystemVariables.name());
		
		LOGGER.debug("{} Systemvariables calculated.", systemVariables.size());
		systemVariableService.update(systemVariables);
		
	}

	

}
