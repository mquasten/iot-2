package de.mq.iot2.weather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.client.RestOperations;

import de.mq.iot2.support.RandomTestUtil;
import de.mq.iot2.weather.MeteorologicalData;

class OpenWeatherRepositoryImplTest {

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	@SuppressWarnings("unchecked")
	private final Converter<Map<String, Object>, MeteorologicalData> meteorologicalDataConverter = Mockito.mock(Converter.class);
	private final String city = RandomTestUtil.randomString();
	private final String country = RandomTestUtil.randomString();
	private final String key = RandomTestUtil.randomString();

	private final WeatherRepository weatherRepository = new OpenWeatherRepositoryImpl(restOperations, meteorologicalDataConverter, city, country, key);

	@Test
	void forecast() {
		final var meteorologicalDataCurrentDay = new MeteorologicalDataImpl(0, 0, 0, 0, 0, ZonedDateTime.now());
		final var meteorologicalDataNextDay = new MeteorologicalDataImpl(1, 1, 1, 1, 1, ZonedDateTime.now().plusDays(1));
		final Map<String, Object> rowNextDay = Map.of("key1", "value1");
		final Map<String, Object> rowCurrentDay = Map.of("key2", "value2");
		final List<Map<String, Object>> forecastList = List.of(rowNextDay, rowCurrentDay);
		final var jsonAsMap = Map.of(OpenWeatherRepositoryImpl.FORECAST_LIST_NODE_NAME, forecastList);
		Mockito.when(meteorologicalDataConverter.convert(rowCurrentDay)).thenReturn(meteorologicalDataCurrentDay);
		Mockito.when(meteorologicalDataConverter.convert(rowNextDay)).thenReturn(meteorologicalDataNextDay);
		Mockito.when(restOperations.getForObject(OpenWeatherRepositoryImpl.OPEN_WEATHER_FORE_CAST_URL, Map.class,
				Map.of(OpenWeatherRepositoryImpl.PARAMETER_CITY, city, OpenWeatherRepositoryImpl.PARAMETER_COUNTRY, country, OpenWeatherRepositoryImpl.PARAMETER_KEY, key)))
				.thenReturn(jsonAsMap);

		final var results = (List<MeteorologicalData>) weatherRepository.forecast();

		assertEquals(2, results.size());
		assertEquals(meteorologicalDataCurrentDay, results.get(0));
		assertEquals(meteorologicalDataNextDay, results.get(1));
	}

	@Test
	void weather() {
		final var meteorologicalData = new MeteorologicalDataImpl(0, 0, 0, 0, 0, ZonedDateTime.now());
		final Map<String, Object> jsonAsMap = Map.of(OpenWeatherRepositoryImpl.MAIN_NODE_NAME, "value1");
		Mockito.when(restOperations.getForObject(OpenWeatherRepositoryImpl.OPEN_WEATHER_WEATHER_URL, Map.class,
				Map.of(OpenWeatherRepositoryImpl.PARAMETER_CITY, city, OpenWeatherRepositoryImpl.PARAMETER_COUNTRY, country, OpenWeatherRepositoryImpl.PARAMETER_KEY, key)))
				.thenReturn(jsonAsMap);
		Mockito.when(meteorologicalDataConverter.convert(jsonAsMap)).thenReturn(meteorologicalData);

		assertEquals(meteorologicalData, weatherRepository.weather());
	}

}
