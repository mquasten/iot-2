package de.mq.iot2.weather.support;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import de.mq.iot2.weather.MeteorologicalData;



class MeteorologicalDataImpl  implements Comparable<MeteorologicalData>, MeteorologicalData{
	
	private final double lowestTemperature;
	private final double temperature;
	private final double highestTemperature;
	private final double 	windVelocityAmount;
	private final ZonedDateTime dateTime;
	

	/*
	 * Degrees from north, right rotating over east (90°)  , south (180°) , west(270°).
	 */
	private final double windVelocityAngleInDegrees;
	
	MeteorologicalDataImpl(final double lowestTemperature, final double temperature, final double highestTemperature, final double windVelocityAmount, final double windVelocityAngleInDegrees, final ZonedDateTime dateTime) {
		this.lowestTemperature = lowestTemperature;
		this.temperature = temperature;
		this.highestTemperature = highestTemperature;
		this.windVelocityAmount = windVelocityAmount;
		this.windVelocityAngleInDegrees = windVelocityAngleInDegrees;
		this.dateTime=dateTime;
	}
	

	@Override
	public final double lowestTemperature() {
		return lowestTemperature;
	}

	
	@Override
	public final double temperature() {
		return temperature;
	}

	
	@Override
	public final double highestTemperature() {
		return highestTemperature;
	}

	
	@Override
	public final double windVelocityAmount() {
		return windVelocityAmount;
	}

	
	@Override
	public final double windVelocityAngleInDegrees() {
		return windVelocityAngleInDegrees;
	}
	
	
	@Override
	public final ZonedDateTime dateTime() {
		return dateTime;
	}

	@Override
	public final int compareTo(final MeteorologicalData other) {
		return dateTime.compareTo(other.dateTime());
	}

	
	
	@Override
	public final boolean hasDate(final LocalDate localDate) {
		return LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth()).equals(localDate);
	}
}
