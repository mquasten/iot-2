package de.mq.iot2.weather.support;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

class MeteorologicalDataMapBuilder {

	private final Map<String, Object> data = new HashMap<>();

	private final Map<String, Number> temperatures = new HashMap<>();

	private final Map<String, Number> wind = new HashMap<>();

	MeteorologicalDataMapBuilder withDateTime(final ZonedDateTime dateTime) {
		data.put(MapToMeteorologicalDataConverterImpl.DATETIME_KEY, dateTime.toEpochSecond());
		return this;

	}

	MeteorologicalDataMapBuilder withTemperature(final Number temperature) {
		temperatures.put(MapToMeteorologicalDataConverterImpl.TEMPERATURE_KEY, temperature);
		return this;
	}

	MeteorologicalDataMapBuilder withLowestTemperature(final Number lowestTemperature) {
		temperatures.put(MapToMeteorologicalDataConverterImpl.LOWEST_TEMPERATURE_KEY, lowestTemperature);
		return this;
	}

	MeteorologicalDataMapBuilder withHighestTemperature(final Number highestTemperature) {
		temperatures.put(MapToMeteorologicalDataConverterImpl.HIGHEST_TEMPERATURE_KEY, highestTemperature);
		return this;
	}

	MeteorologicalDataMapBuilder withWindVelocityAmount(final Number windVelocityAmount) {
		wind.put(MapToMeteorologicalDataConverterImpl.AMOUNT_WIND_VELOCITY_KEY, windVelocityAmount);
		return this;
	}

	MeteorologicalDataMapBuilder withWindVelocityAngle(final Number windVelocityAngle) {
		wind.put(MapToMeteorologicalDataConverterImpl.DEGREES_WIND_VELOCITY_KEY, windVelocityAngle);
		return this;
	}

	Map<String, Object> build() {
		dataExistsGuard(data, MapToMeteorologicalDataConverterImpl.DATETIME_KEY);
		dataExistsGuard(temperatures, Arrays.asList(MapToMeteorologicalDataConverterImpl.TEMPERATURE_KEY, MapToMeteorologicalDataConverterImpl.LOWEST_TEMPERATURE_KEY,
				MapToMeteorologicalDataConverterImpl.HIGHEST_TEMPERATURE_KEY));
		dataExistsGuard(wind, Arrays.asList(MapToMeteorologicalDataConverterImpl.AMOUNT_WIND_VELOCITY_KEY));

		data.put(MapToMeteorologicalDataConverterImpl.MAIN_DATA_KEY, temperatures);
		data.put(MapToMeteorologicalDataConverterImpl.WIND_DATA_KEY, wind);
		return data;
	}

	private void dataExistsGuard(Map<?, ?> data, final Collection<String> keys) {
		keys.forEach(key -> dataExistsGuard(data, key));
	}

	private void dataExistsGuard(Map<?, ?> data, final String key) {
		Assert.isTrue(data.containsKey(key), String.format("%s is required.", key));
	}

	static MeteorologicalDataMapBuilder builder() {
		return new MeteorologicalDataMapBuilder();
	}

}
