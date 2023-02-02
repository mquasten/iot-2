package de.mq.iot2.sysvars.support;

import java.time.LocalTime;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
class LocalTimeValidatorImpl implements ConstraintValidator<ValidTime, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {

		if (!StringUtils.hasText(value)) {
			return true;
		}

		if (value.trim().length() != 5) {
			return false;
		}

		final String[] values = value.split("[:]");
		if (values.length != 2) {
			return false;
		}

		try {
			LocalTime.of(Integer.parseInt(values[0]), Integer.parseInt(values[0]));
			return true;
		} catch (Exception ex) {
			return false;
		}

	}

}
