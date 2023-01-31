package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.sysvars.SystemVariables;

@Component
class VariableModelMapper implements ModelMapper<SystemVariables, VariableModel>{

	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	
	VariableModelMapper(final CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService=configurationService;
	}

	@Override
	public VariableModel toWeb(final  SystemVariables domain) {
		
		final TwilightType twilightType = (TwilightType) configurationService.parameter(RuleKey.EndOfDay, Key.SunUpDownType).orElse(TwilightType.Mathematical);
		final var date = LocalDate.now();
		final var defaultTime= LocalTime.of(0, 0);
		final Pair<LocalTime, LocalTime> sunUpDownToday =  Pair.of(calendarService.sunUpTime(date, twilightType).orElse(defaultTime) ,calendarService.sunDownTime(date, twilightType).orElse(defaultTime)) ;
		final Pair<LocalTime, LocalTime> sunUpDownTomorrow =  Pair.of(calendarService.sunUpTime(date.plusDays(1), twilightType).orElse(defaultTime) ,calendarService.sunDownTime(date.plusDays(1), twilightType).orElse(defaultTime)) ;
		final VariableModel variableModel = new VariableModel(date, twilightType, sunUpDownToday, sunUpDownTomorrow);
	
		return variableModel;
	}

	

}
