package de.mq.iot2.configuration.support;

import java.util.Map;
import java.util.function.Predicate;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.configuration.Parameter.Key;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
class ParameterValidatorImpl implements ConstraintValidator<ValidParameter, ParameterModel> {

	static final String MESSAGE_KEY = "{parameter.value.%s.message}";

	private final ConversionService conversionService;

	private final Map<Key, Predicate<String>> additionalValidations;

	ParameterValidatorImpl(final ConversionService conversionService) {
		this.conversionService = conversionService;
		additionalValidations = Map.of(Key.DaysBack, value -> inRange(conversionService.convert(value, Integer.class), 1, 999), Key.ShadowTemperature,
				value -> inRange(conversionService.convert(value, Double.class), 10d, 40d));
	}

	private boolean inRange(final Number value, final Number min, final Number max) {
		return value.doubleValue() <= max.doubleValue() && value.doubleValue() >= min.doubleValue();

	}

	@Override
	public boolean isValid(final ParameterModel value, final ConstraintValidatorContext context) {
		Assert.hasText(value.getName(), "Name is required.");
		final Key key = Key.valueOf(value.getName());
		context.disableDefaultConstraintViolation();
		try {
			conversionService.convert(value.getValue(), key.type());
			final var result = isValid(key, value.getValue());
			if (!result) {
				context.buildConstraintViolationWithTemplate(String.format(MESSAGE_KEY, key.name().toLowerCase())).addPropertyNode("value").addConstraintViolation();
			}
			return result;
		} catch (final ConversionFailedException ex) {
			context.buildConstraintViolationWithTemplate(String.format(MESSAGE_KEY, key.type().getSimpleName().toLowerCase())).addPropertyNode("value").addConstraintViolation();
			return false;
		}
	}

	private boolean isValid(final Key key, final String value) {

		if (!additionalValidations.containsKey(key)) {
			return true;
		}
		return additionalValidations.get(key).test(value);
	}

}
