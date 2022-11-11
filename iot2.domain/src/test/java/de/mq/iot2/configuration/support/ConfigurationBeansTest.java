package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class ConfigurationBeansTest {

	private static final String TIME_STRING = "11:11";
	private ConfigurationBeans configurationBeans = new ConfigurationBeans();

	@Test
	void conversionService() {
		assertEquals(LocalTime.parse(TIME_STRING), configurationBeans.conversionService().convert(TIME_STRING, LocalTime.class));

	}

}
