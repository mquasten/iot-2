package de.mq.iot2.configuration.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;

@Configuration
class ConfigurationBeans {
	final static String EMPTY_OPTIONAL_STRING = "<Empty>";

	@Bean
	ConversionService conversionService() {
		final var conversionService = new DefaultConversionService();
		conversionService.addConverter(String.class, LocalTime.class, localTimeHH24MIConverter(conversionService));
		conversionService.addConverter(LocalTime.class, String.class, localTime2StringConverter());
		conversionService.addConverter(LocalDate.class, String.class, localDate2StringConverter());
		conversionService.addConverter(optional2StringConverter(conversionService));
		conversionService.addConverter(Cycle.class, String.class, cycle2StringConverter());
		return conversionService;
	}

	private final Converter<String, LocalTime> localTimeHH24MIConverter(ConversionService conversionService) {
		return new Converter<String, LocalTime>() {

			@Override
			public LocalTime convert(@Nullable String source) {
				Assert.notNull(source, "Value required.");
				String values[] = source.split(":", 3);
				Assert.isTrue(values.length == 2, "LocalTime format 'HH24:MI' expected.");
				return LocalTime.of(conversionService.convert(values[0], Integer.class), conversionService.convert(values[1], Integer.class));
			}

		};

	}

	private final Converter<LocalTime, String> localTime2StringConverter() {
		return new Converter<LocalTime, String>() {
			@Override
			public String convert(@Nullable LocalTime source) {
				Assert.notNull(source, "Value required.");
				return source.format(DateTimeFormatter.ofPattern("HH:mm"));
			}

		};

	}

	private final Converter<LocalDate, String> localDate2StringConverter() {
		return new Converter<LocalDate, String>() {
			@Override
			public String convert(@Nullable final LocalDate source) {
				return source.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			}
		};

	}

	private final Converter<Cycle, String> cycle2StringConverter() {
		return new Converter<Cycle, String>() {
			@Override
			public String convert(@Nullable final Cycle source) {
				return source.name();
			}
		};
	}

	private final Converter<Optional<?>, String> optional2StringConverter(final ConversionService conversionService) {
		return new Converter<Optional<?>, String>() {
			@Override
			public String convert(@Nullable Optional<?> source) {
				if (source.isEmpty()) {
					return EMPTY_OPTIONAL_STRING;
				}
				return conversionService.convert(source.get(), String.class);

			}

		};

	}
}
