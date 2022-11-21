package de.mq.iot2.weather.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import de.mq.iot2.weather.MeteorologicalData;

@Repository
public class OpenWeatherRepositoryImpl implements WeatherRepository  {
	
	
	
	static final String PARAMETER_KEY = "key";
	static final String PARAMETER_COUNTRY = "country";
	static final String PARAMETER_CITY = "city";
	final static String OPEN_WEATHER_FORE_CAST_URL  = "http://api.openweathermap.org/data/2.5/forecast?q={city},{country}&appid={key}&units=metric";
	final static String OPEN_WEATHER_WEATHER_URL  = "http://api.openweathermap.org/data/2.5/weather?q={city},{country}&appid={key}&units=metric";
	
	static final String FORECAST_LIST_NODE_NAME = "list";
	static final String MAIN_NODE_NAME = "main";

	private final Converter<Map<String,Object> , MeteorologicalData> meteorologicalDataConverter;
	private final RestOperations restOperations;
	private final String city;
	private final String country;
	private final String key;
	
	OpenWeatherRepositoryImpl(final RestOperations restOperations,  final @Qualifier("meteorologicalDataConverter")  Converter<Map<String,Object> , MeteorologicalData>  meteorologicalDataConverter,
			@Value("${iot2.openweather.city}") final String city, @Value("${iot2.openweather.country:de}") final String country,@Value("${iot2.openweather.key}") final String key ) {
		this.restOperations = restOperations;
		this.meteorologicalDataConverter=meteorologicalDataConverter;
		this.city=city;
		this.country=country;
		this.key=key;
	}
	
	@Override
	public  Collection<MeteorologicalData>   forecast() {
		@SuppressWarnings("unchecked")
		final Map<String, Object> res = restOperations.getForObject(OPEN_WEATHER_FORE_CAST_URL, Map.class, Map.of(PARAMETER_CITY , city , PARAMETER_COUNTRY, country ,PARAMETER_KEY ,key ));
		Assert.isTrue(res.containsKey(FORECAST_LIST_NODE_NAME), "Invalid JSON: Node list is missing.");
		@SuppressWarnings("unchecked")
		final List<Map<String, Object>> list = (List<Map<String, Object>>) res.get(FORECAST_LIST_NODE_NAME);
		return list.stream().map(map -> meteorologicalDataConverter.convert(map)).sorted().collect(Collectors.toList()); 
	}

	
	@Override
	public MeteorologicalData weather() {
		final Map<String, Object> parameter= Map.of(PARAMETER_CITY , city , PARAMETER_COUNTRY, country ,PARAMETER_KEY ,key );
		@SuppressWarnings("unchecked")
		final Map<String, Object> res = restOperations.getForObject(OPEN_WEATHER_WEATHER_URL, Map.class, parameter);
		Assert.notNull(res.get(MAIN_NODE_NAME), "Main node is required.");
		return meteorologicalDataConverter.convert(res);
	}


}
