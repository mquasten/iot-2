package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.MonthDay;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
class DayValidatorConverterImpl implements ConstraintValidator<ValidDayModel, DayModel> {

	private static final String DATE_DELIMITER_ENGLISH = "/";
	private static final String VALUE_FIELD_NAME = "value";
	final Map<Class<?>, BiFunction<DayModel, ConstraintValidatorContext, Boolean>> converters = Map.of(DayOfWeekDayImpl.class,
			(dayModel, context) -> dayOfWeekConverter(dayModel, context), DayOfMonthImpl.class, (dayModel, context) -> dayOfMonthConverter(dayModel, context));

	@Override
	public boolean isValid(final DayModel dayModel, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		Assert.notNull(dayModel.getType(), "Type is required.");
		final Class<?> targetEntity = dayModel.targetEntity();
		dayModel.setTargetValue(null);
		Assert.isTrue(converters.containsKey(targetEntity), "Converter undefined.");
		return converters.get(targetEntity).apply(dayModel, context);
	}

	private Boolean dayOfWeekConverter(final DayModel dayModel, final ConstraintValidatorContext context) {
		if (!StringUtils.hasText(dayModel.getValue())) {
			context.buildConstraintViolationWithTemplate("{error.mandatory}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}

		try {
			dayModel.setTargetValue(DayOfWeek.of(Integer.parseInt(dayModel.getValue())));
			return true;
		} catch (final Exception ex) {
			context.buildConstraintViolationWithTemplate("{error.dayofweek}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}

	}

	private Boolean dayOfMonthConverter(final DayModel dayModel, final ConstraintValidatorContext context) {
		if (!StringUtils.hasText(dayModel.getValue())) {
			context.buildConstraintViolationWithTemplate("{error.mandatory}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}

		if (dayModel.getValue().contains(DATE_DELIMITER_ENGLISH)) {
			return monthDayEnglish(dayModel, context);
		}

		return monthDayGerman(dayModel, context);

	}

	private boolean monthDayEnglish(final DayModel dayModel, final ConstraintValidatorContext context) {
		final String[] values = dayModel.getValue().split(DATE_DELIMITER_ENGLISH);
		if (values.length != 2) {
			context.buildConstraintViolationWithTemplate("{error.dayofmonth}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}
		try {

			dayModel.setTargetValue(MonthDay.of(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
			return true;
		} catch (Exception ex) {
			context.buildConstraintViolationWithTemplate("{error.dayofmonth}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}
	}

	private boolean monthDayGerman(final DayModel dayModel, final ConstraintValidatorContext context) {
		final String[] values = dayModel.getValue().split("[.]");
		if (values.length != 2) {
			context.buildConstraintViolationWithTemplate("{error.dayofmonth}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}
		try {
			dayModel.setTargetValue(MonthDay.of(Integer.parseInt(values[1]), Integer.parseInt(values[0])));
			return true;
		} catch (final Exception ex) {
			context.buildConstraintViolationWithTemplate("{error.dayofmonth}").addPropertyNode(VALUE_FIELD_NAME).addConstraintViolation();
			return false;
		}
	}
}
