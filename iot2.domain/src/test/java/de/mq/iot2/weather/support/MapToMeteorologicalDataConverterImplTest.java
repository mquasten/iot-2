package de.mq.iot2.weather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.iot2.weather.MeteorologicalData;

class MapToMeteorologicalDataConverterImplTest {

	static final ZonedDateTime TIME = ZonedDateTime.now(MapToMeteorologicalDataConverterImpl.ZONE_OFFSET);
	static final Double WIND_VELOCITY_DEGREES = 102.001;
	static final Double WIND_VELOCITY_AMOUNT = 7.11d;
	static final Double MAX_TEMPERATURE = 27.61d;
	static final Double TEMPERATURE = 26.50;
	static final Double MIN_TEMPERATURE = 25.6d;

	private final Converter<Map<String, Object>, MeteorologicalData> converter = new MapToMeteorologicalDataConverterImpl();

	@Test
	void convert() {
		final Map<String, Object> map = MeteorologicalDataMapBuilder.builder().withDateTime(TIME).withLowestTemperature(MIN_TEMPERATURE).withTemperature(TEMPERATURE)
				.withHighestTemperature(MAX_TEMPERATURE).withWindVelocityAmount(WIND_VELOCITY_AMOUNT).withWindVelocityAngle(WIND_VELOCITY_DEGREES).build();

		final MeteorologicalData result = converter.convert(map);

		assertNotNull(result);
		assertEquals(TIME.withNano(0), result.dateTime());
		assertEquals(MIN_TEMPERATURE.doubleValue(), result.lowestTemperature());
		assertEquals(TEMPERATURE.doubleValue(), result.temperature());
		assertEquals(MAX_TEMPERATURE.doubleValue(), result.highestTemperature());
		assertEquals(WIND_VELOCITY_AMOUNT.doubleValue(), result.windVelocityAmount());
		assertEquals(WIND_VELOCITY_DEGREES.doubleValue(), result.windVelocityAngleInDegrees());
	}

	@Test
	void convertInt() {
		final Map<String, Object> map = MeteorologicalDataMapBuilder.builder().withDateTime(TIME).withLowestTemperature(MIN_TEMPERATURE.intValue())
				.withTemperature(TEMPERATURE.intValue()).withHighestTemperature(MAX_TEMPERATURE.intValue()).withWindVelocityAmount(WIND_VELOCITY_AMOUNT.intValue())
				.withWindVelocityAngle(WIND_VELOCITY_DEGREES.intValue()).build();

		final MeteorologicalData result = converter.convert(map);

		assertNotNull(result);
		assertEquals(TIME.withNano(0), result.dateTime());
		assertEquals(MIN_TEMPERATURE.intValue(), result.lowestTemperature());
		assertEquals(TEMPERATURE.intValue(), result.temperature());
		assertEquals(MAX_TEMPERATURE.intValue(), result.highestTemperature());
		assertEquals(WIND_VELOCITY_AMOUNT.intValue(), result.windVelocityAmount());
		assertEquals(WIND_VELOCITY_DEGREES.intValue(), result.windVelocityAngleInDegrees());
	}

	@Test
	void convertWindSpeedAngleMissing() {
		final Map<String, Object> map = MeteorologicalDataMapBuilder.builder().withDateTime(TIME).withLowestTemperature(MIN_TEMPERATURE).withTemperature(TEMPERATURE)
				.withHighestTemperature(MAX_TEMPERATURE).withWindVelocityAmount(WIND_VELOCITY_AMOUNT).build();

		final MeteorologicalData result = converter.convert(map);

		assertNotNull(result);
		assertEquals(TIME.withNano(0), result.dateTime());
		assertEquals(MIN_TEMPERATURE.doubleValue(), result.lowestTemperature());
		assertEquals(TEMPERATURE.doubleValue(), result.temperature());
		assertEquals(MAX_TEMPERATURE.doubleValue(), result.highestTemperature());
		assertEquals(WIND_VELOCITY_AMOUNT.doubleValue(), result.windVelocityAmount());
		assertEquals(0d, result.windVelocityAngleInDegrees());
	}

}
