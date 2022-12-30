package de.mq.iot2.weather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.weather.MeteorologicalData;
import de.mq.iot2.weather.WeatherService;

class WeatherSerciceImplTest {
	private final LocalDate localDate = LocalDate.now();
	private final WeatherRepository weatherRepository = Mockito.mock(WeatherRepository.class);
	private final WeatherService weatherService = new WeatherServiceImpl(weatherRepository);

	@Test
	void maxForecastTemperature() {
		final Collection<MeteorologicalData> meteorologicalData = List.of(newMeteorologicalData(10), newMeteorologicalData(20), newMeteorologicalData(15));
		Mockito.when(weatherRepository.forecast()).thenReturn(meteorologicalData);
		assertEquals(Optional.of(20d), weatherService.maxForecastTemperature(localDate));

	}

	private MeteorologicalDataImpl newMeteorologicalData(final double temperature) {
		return new MeteorologicalDataImpl(0, temperature, 0, 0, 0, ZonedDateTime.of(localDate, LocalTime.now(), ZoneId.systemDefault()));
	}
}
