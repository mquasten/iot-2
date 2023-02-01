package de.mq.iot2.sysvars.support;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.sysvars.SystemVariables;
import de.mq.iot2.weather.WeatherService;

@Component
class VariableModelMapper implements ModelMapper<SystemVariables, VariableModel>{

	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	
	private final WeatherService weatherService;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private final ConversionService conversionService;
	
	VariableModelMapper(final CalendarService calendarService, final ConfigurationService configurationService, final WeatherService weatherService, ConversionService conversionService) {
		this.calendarService = calendarService;
		this.configurationService=configurationService;
		this.weatherService=weatherService;
		this.conversionService=conversionService;
	}

	@Override
	public VariableModel toWeb(final  SystemVariables domain) {
		
		final var twilightType = configurationService.parameter(RuleKey.EndOfDay, Key.SunUpDownType, TwilightType.class).orElse(TwilightType.Mathematical);
		final VariableModel variableModel = new VariableModel();
		variableModel.setTwilightType(twilightType.name().toLowerCase());
		
		
		calendarService.sunUpTime(variableModel.getDate(), twilightType).ifPresent(time -> variableModel.setSunUpToday(format(time)));
		calendarService.sunDownTime(variableModel.getDate(), twilightType).ifPresent(time -> variableModel.setSunDownToday(format(time)));
		calendarService.sunUpTime(variableModel.getDate().plusDays(1), twilightType).ifPresent(time -> variableModel.setSunUpTomorrow(format(time)));
		calendarService.sunDownTime(variableModel.getDate().plusDays(1), twilightType).ifPresent(time -> variableModel.setSunDownTomorrow(format(time)));
		weatherService.maxForecastTemperature(variableModel.getDate()).ifPresent(temperature -> variableModel.setMaxTemperatureToday(conversionService.convert(temperature, String.class)));
		weatherService.maxForecastTemperature(variableModel.getDate().plusDays(1)).ifPresent(temperature -> variableModel.setMaxTemperatureTomorrow(conversionService.convert(temperature, String.class)));
		return variableModel;
	}

	private String format(final LocalTime time) {
		return time.format(dateTimeFormatter);
	}

	

}
