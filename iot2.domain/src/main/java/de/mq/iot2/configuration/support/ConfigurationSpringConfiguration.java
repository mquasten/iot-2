package de.mq.iot2.configuration.support;

import java.time.LocalTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
class ConfigurationSpringConfiguration{
	@Bean
	ConversionService conversionService() {
		DefaultConversionService conversionService= new DefaultConversionService();
		conversionService.addConverter(String.class, LocalTime.class, source -> {
			return LocalTime.parse(source);
		});
		return conversionService;
	}

}
