package de.mq.iot2.weather;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface MeteorologicalData {

	double lowestTemperature();

	double temperature();

	double highestTemperature();

	double windVelocityAmount();

	double windVelocityAngleInDegrees();

	ZonedDateTime dateTime();

	boolean hasDate(LocalDate localDate);

}