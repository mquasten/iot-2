package de.mq.iot2.weather.support;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.mq.iot2.weather.MeteorologicalData;

@Service
class WeatherSerciceImpl implements WeatherService {
	
	private final WeatherRepository weatherRepository;

	WeatherSerciceImpl(WeatherRepository weatherRepository) {
		this.weatherRepository = weatherRepository;
	}
	
	@Override
	public Optional<Double> maxForecastTemperature(final LocalDate date) {
		return  weatherRepository.forecast().stream().filter(weather -> weather.hasDate(date)).map(MeteorologicalData::temperature).sorted((t1,t2) -> t2.compareTo(t1) ).findFirst();
	}

}
