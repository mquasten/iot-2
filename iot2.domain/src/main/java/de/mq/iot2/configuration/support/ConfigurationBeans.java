package de.mq.iot2.configuration.support;

import java.time.LocalTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Configuration
class ConfigurationBeans {
	@Bean
	ConversionService conversionService() {
		final var conversionService = new DefaultConversionService();
		conversionService.addConverter(String.class, LocalTime.class, localTimeHH24MIConverter(conversionService));
		return conversionService;
	}
	
	final Converter<String,LocalTime> localTimeHH24MIConverter(ConversionService conversionService) {
		return new Converter<String, LocalTime>() {

			@Override
			public LocalTime convert(@Nullable String source) {
				Assert.notNull(source, "Value required.");
				String values[] = source.split(":",3);
				Assert.isTrue(values.length ==2 , "LocalTime format 'HH24:MI' expected." );
			
				return LocalTime.of( conversionService.convert(values[0], Integer.class),conversionService.convert(values[1], Integer.class) ); 
			}
			
		};
		
	}

}
