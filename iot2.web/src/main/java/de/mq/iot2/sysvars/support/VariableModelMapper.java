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
import de.mq.iot2.weather.WeatherService;

@Component
class VariableModelMapper implements ModelMapper<SystemVariables, VariableModel>{

	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	
	private final WeatherService weatherService;
	
	VariableModelMapper(final CalendarService calendarService, final ConfigurationService configurationService, final WeatherService weatherService) {
		this.calendarService = calendarService;
		this.configurationService=configurationService;
		this.weatherService=weatherService;
	}

	@Override
	public VariableModel toWeb(final  SystemVariables domain) {
		
		final var twilightType = configurationService.parameter(RuleKey.EndOfDay, Key.SunUpDownType, TwilightType.class).orElse(TwilightType.Mathematical);
		final var date = LocalDate.now();
		final var defaultUpTime= LocalTime.of(0, 0);
		final var defaultDownTime= LocalTime.of(23, 59);
		final Pair<LocalTime, LocalTime> sunUpDownToday =  Pair.of(calendarService.sunUpTime(date, twilightType).orElse(defaultUpTime) ,calendarService.sunDownTime(date, twilightType).orElse(defaultDownTime)) ;
		final Pair<LocalTime, LocalTime> sunUpDownTomorrow =  Pair.of(calendarService.sunUpTime(date.plusDays(1), twilightType).orElse(defaultUpTime) ,calendarService.sunDownTime(date.plusDays(1), twilightType).orElse(defaultDownTime)) ;
	
		final var maxTemperatureToday= weatherService.maxForecastTemperature(date).orElse(Double.NaN);
		final var maxTemperatureTomorrow= weatherService.maxForecastTemperature(date.plusDays(1)).orElse(Double.NaN);
		final VariableModel variableModel = new VariableModel(date, twilightType, sunUpDownToday, sunUpDownTomorrow, maxTemperatureToday, maxTemperatureTomorrow);
	
		
		return variableModel;
	}

	

}
