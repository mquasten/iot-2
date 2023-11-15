package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.rules.EndOfDayArguments;
import de.mq.iot2.rules.RuleService;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;
import de.mq.iot2.weather.WeatherService;

@Service
public class EndOfDayBatchImpl {
	private static final String END_OF_DAY_UPDATE_BATCH_NAME = "end-of-day-update";
	private static final String END_OF_DAY_BATCH_NAME = "end-of-day";
	private static Logger LOGGER = LoggerFactory.getLogger(EndOfDayBatchImpl.class);
	private final CalendarService calendarService;

	private final ConfigurationService configurationService;

	private final RuleService ruleService;

	private final SystemVariableService systemVariableService;

	private final WeatherService weatherService;
	

	private final ProtocolService protocolService;

	EndOfDayBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService, final RuleService ruleService,
			final SystemVariableService systemVariableService, final WeatherService weatherService, final ProtocolService protocolService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
		this.ruleService = ruleService;
		this.systemVariableService = systemVariableService;
		this.weatherService = weatherService;
		this.protocolService=protocolService;
	}

	@BatchMethod(value = END_OF_DAY_BATCH_NAME, converterClass = EndOfDayBatchArgumentConverterImpl.class)
	final void execute(final LocalDate date) {
		final Protocol protocol = protocolService.create(END_OF_DAY_BATCH_NAME);
		executeWithCatch(protocol, date, Optional.empty());

	}

	private void executeWithCatch(final Protocol protocol,  final LocalDate date, final Optional<LocalTime> uptateTime) {
		try {
			execute(protocol, date, uptateTime);
		} catch ( RuntimeException exception) {
			protocolService.error(protocol, exception);
			throw exception;
		}
	}
	
	private void execute(final Protocol protocol,  final LocalDate date, final Optional<LocalTime> uptateTime) {
		
		final Cycle cycle = calendarService.cycle(date);

		final var parameters = configurationService.parameters(RuleKey.EndOfDay, cycle);

		// final TimeType timeType = calendarService.timeType(date);

		final var twilightType = parameters.containsKey(Key.SunUpDownType) ? (TwilightType) parameters.get(Key.SunUpDownType) : TwilightType.Mathematical;

		final var sunUpTime = calendarService.sunUpTime(date, twilightType);

		final var sunDownTime = calendarService.sunDownTime(date, twilightType);

		final var maxForecastTemperature = weatherService.maxForecastTemperature(date);

		final var arguments = Map.of(EndOfDayArguments.Date, date, EndOfDayArguments.SunUpTime, sunUpTime, EndOfDayArguments.SunDownTime, sunDownTime, EndOfDayArguments.Cycle,
				cycle, EndOfDayArguments.MaxForecastTemperature, maxForecastTemperature, EndOfDayArguments.UpdateTime, uptateTime);

		protocolService.assignParameter(protocol, ProtocolParameterType.Configuration, parameters);
		protocolService.assignParameter(protocol, ProtocolParameterType.RulesEngineArgument, arguments);
		
		LOGGER.debug("Start RulesEngine parameters {} arguments {}.", parameters, arguments);
		final var results = ruleService.process(parameters, arguments);
		
		Assert.notNull(results.containsKey(EndOfDayArguments.SystemVariables.name()), "Systemvariables required.");

		@SuppressWarnings("unchecked")
		final Collection<SystemVariable> systemVariables = (Collection<SystemVariable>) results.get(EndOfDayArguments.SystemVariables.name());
		protocolService.assignParameter(protocol, systemVariables);
		Assert.notEmpty(systemVariables, "Systemvariables required.");
		LOGGER.debug("{} Systemvariables calculated.", systemVariables.size());
		
		
		final var updatedSystemVariables =systemVariableService.update(systemVariables);
		
		protocolService.updateSystemVariables(protocol, updatedSystemVariables);
		
		protocolService.success(protocol);
		
		
	}

	@BatchMethod(value = END_OF_DAY_UPDATE_BATCH_NAME, converterClass = EndOfDayUpdateBatchArgumentConverterImpl.class)
	final void executeUpdate(final LocalTime time) {
		final Protocol protocol = protocolService.create(END_OF_DAY_UPDATE_BATCH_NAME);
		executeWithCatch(protocol, LocalDate.now(), Optional.of(time));

	}

}
