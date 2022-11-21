package de.mq.iot2.weather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import de.mq.iot2.weather.MeteorologicalData;

class MeteorologicalDataImplTest {

	private final double lowestTemperature = random();

	private final double temperature = random();

	private final double highestTemperature = random();

	private final double windVelocityAmount = random();

	private final double windVelocityAngleInDegrees = 12d * random();

	private final ZonedDateTime dateTime = ZonedDateTime.now();

	private final MeteorologicalData meteorologicalData = new MeteorologicalDataImpl(lowestTemperature, temperature,
			highestTemperature, windVelocityAmount, windVelocityAngleInDegrees, dateTime);

	private final double random() {
		return Math.random() * 30d;
	}

	@Test
	final void lowestTemperature() {
		assertEquals(lowestTemperature, meteorologicalData.lowestTemperature());
	}

	@Test
	final void temperature() {
		assertEquals(temperature, meteorologicalData.temperature());
	}

	@Test
	final void highestTemperature() {
		assertEquals(highestTemperature, meteorologicalData.highestTemperature());
	}

	@Test
	final void windVelocityAmount() {
		assertEquals(windVelocityAmount, meteorologicalData.windVelocityAmount());
	}

	@Test
	final void windVelocityAngleInDegrees() {
		assertEquals(windVelocityAngleInDegrees, meteorologicalData.windVelocityAngleInDegrees());
	}

	@Test
	final void dateTime() {
		assertEquals(dateTime, meteorologicalData.dateTime());
	}

	@Test
	final void compareTo1() {
		assertEquals(0, new MeteorologicalDataImpl(random(), random(), random(), random(), 12 * random(), dateTime)
				.compareTo(meteorologicalData));
		assertEquals(-1,
				new MeteorologicalDataImpl(random(), random(), random(), random(), 12 * random(), dateTime.minusDays(2))
						.compareTo(meteorologicalData));
		assertEquals(1,
				new MeteorologicalDataImpl(random(), random(), random(), random(), 12 * random(), dateTime.plusDays(2))
						.compareTo(meteorologicalData));
	}

	@Test
	final void hasDate() {
		assertTrue(meteorologicalData.hasDate(dateTime.toLocalDate()));
		assertFalse(meteorologicalData.hasDate(dateTime.toLocalDate().minusDays(1)));
	}

}
