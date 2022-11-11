package de.mq.iot2.configuration.support;

import java.time.LocalTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
class ConfigurationBeans {
	@Bean
	ConversionService conversionService() {
		final var conversionService = new DefaultConversionService();
		conversionService.addConverter(String.class, LocalTime.class, LocalTime::parse);
		return conversionService;
	}

}
