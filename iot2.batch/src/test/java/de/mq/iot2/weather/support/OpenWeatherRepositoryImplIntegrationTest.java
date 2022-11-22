package de.mq.iot2.weather.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import de.mq.iot2.weather.MeteorologicalData;
@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestSystemVariablesConfiguration.class, OpenWeatherRepositoryImpl.class,
		MapToMeteorologicalDataConverterImpl.class })
class OpenWeatherRepositoryImplIntegrationTest {

	@Autowired
	private WeatherRepository openWeatherRepository;

	@Test
	void forecast() {

		final Collection<MeteorologicalData> results = openWeatherRepository.forecast();
		// results.forEach(m -> System.out.println(m.dateTime()+":" + m.temperature()));
		
		assertEquals(8, results.stream().filter(m -> m.hasDate(LocalDate.now().plusDays(1)))
				.collect(Collectors.toList()).size());
		assertEquals(40, results.size());
	}

	@Test
	void weather() {
		final MeteorologicalData meteorologicalData = openWeatherRepository.weather();
		assertTrue(ZonedDateTime.now().toEpochSecond() - meteorologicalData.dateTime().toEpochSecond() < 1000);
		//System.out.println(meteorologicalData.dateTime());
	}

}

@ComponentScan("de.mq.iot2.sysvars.support")
class TestSystemVariablesConfiguration {
	
}
