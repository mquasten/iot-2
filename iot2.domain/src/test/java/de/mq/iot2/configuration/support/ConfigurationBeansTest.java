package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.convert.ConversionException;


class ConfigurationBeansTest {


	private ConfigurationBeans configurationBeans = new ConfigurationBeans();

	@Test
	void conversionService() {
		assertEquals(LocalTime.of(11, 11), configurationBeans.conversionService().convert("11:11", LocalTime.class));
		assertEquals(LocalTime.of(1, 1), configurationBeans.conversionService().convert("01:01", LocalTime.class));
		assertEquals(LocalTime.of(1, 1), configurationBeans.conversionService().convert("1:1", LocalTime.class));
	}
	
	@ParameterizedTest
    @ValueSource(strings = {"11.11:11", "11" ,"x:11" ,""})
	void conversionServiceInvalid(final String value) {
		assertThrows(ConversionException.class, () -> configurationBeans.conversionService().convert(value, LocalTime.class));
	
	}
	
	

}
