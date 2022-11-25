package de.mq.iot2.weather.support;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherService {

	Optional<Double> maxForecastTemperature(final LocalDate date);

}