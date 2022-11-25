package de.mq.iot2.weather.support;

import java.util.Collection;

import de.mq.iot2.weather.MeteorologicalData;

interface WeatherRepository {

	Collection<MeteorologicalData> forecast();

	MeteorologicalData weather();

}