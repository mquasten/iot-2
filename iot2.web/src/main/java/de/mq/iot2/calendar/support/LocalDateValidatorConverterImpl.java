package de.mq.iot2.calendar.support;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
class LocalDateValidatorConverterImpl implements ConstraintValidator<ValidLocalDateModel, LocalDateModel> {

	static final String MESSAGE_KEY_DAY_LIMIT = "{error.date.limit}";
	static final String MESSAGE_KEY_TO_BEFORE_FROM = "{error.date.tobeforefrom}";
	static final String MESSAGE_KEY_DATE_FUTURE = "{error.date.future}";
	static final String MESSAGE_KEY_MANDATORY = "{error.mandatory}";
	static final String TO_PROPERTY = "to";
	static final String FROM_PROPERY = "from";
	static final String MESSAGE_KEY_INVALID_DATE = "{error.date}";
	private final long dayLimit;
	private static final String DATE_DELIMITER_ENGLISH = "/";

	LocalDateValidatorConverterImpl(@Value("${iot2.calendar.dayslimit:30}") final long dayLimit) {
		this.dayLimit = dayLimit;
	}

	private ImmutablePair<Boolean, LocalDate> isValid(final String value) {

		if (!StringUtils.hasText(value)) {
			return new ImmutablePair<Boolean, LocalDate>(true, null);
		}
		if (value.contains(DATE_DELIMITER_ENGLISH)) {
			return dateEnglish(value);
		}

		return dateGerman(value);

	}

	private ImmutablePair<Boolean, LocalDate> dateEnglish(final String value) {
		final String[] values = value.split(DATE_DELIMITER_ENGLISH);
		if (values.length != 3) {
			return new ImmutablePair<>(false, null);
		}
		try {

			return new ImmutablePair<>(true, LocalDate.of(Integer.parseInt(values[2]), Integer.parseInt(values[0]), Integer.parseInt(values[1])));
		} catch (Exception ex) {
			return new ImmutablePair<>(false, null);
		}

	}

	private ImmutablePair<Boolean, LocalDate> dateGerman(final String value) {
		final String[] values = value.split("[.]");
		if (values.length != 3) {
			return new ImmutablePair<>(false, null);
		}
		try {

			return new ImmutablePair<>(true, LocalDate.of(Integer.parseInt(values[2]), Integer.parseInt(values[1]), Integer.parseInt(values[0])));
		} catch (Exception ex) {
			return new ImmutablePair<>(false, null);
		}

	}

	@Override
	public boolean isValid(final LocalDateModel localDateModel, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		localDateModel.setFromDate(null);
		localDateModel.setToDate(null);
		final ImmutablePair<Boolean, LocalDate> from = isValid(localDateModel.getFrom());
		final ImmutablePair<Boolean, LocalDate> to = isValid(localDateModel.getTo());

		if (!from.left) {
			context.buildConstraintViolationWithTemplate(MESSAGE_KEY_INVALID_DATE).addPropertyNode(FROM_PROPERY).addConstraintViolation();
		}

		if (!to.left) {
			context.buildConstraintViolationWithTemplate(MESSAGE_KEY_INVALID_DATE).addPropertyNode(TO_PROPERTY).addConstraintViolation();
		}

		final boolean fromAware = StringUtils.hasText(localDateModel.getFrom());
		if (!fromAware) {
			context.buildConstraintViolationWithTemplate(MESSAGE_KEY_MANDATORY).addPropertyNode(FROM_PROPERY).addConstraintViolation();
		}

		if (!from.left || !to.left || !fromAware) {
			return false;
		}

		final LocalDate toDate = to.right != null ? to.right : from.right;

		final boolean fromInFuture = from.right.isAfter(LocalDate.now());
		if (!fromInFuture) {
			context.buildConstraintViolationWithTemplate(MESSAGE_KEY_DATE_FUTURE).addPropertyNode(FROM_PROPERY).addConstraintViolation();
		}

		final boolean toAfterOrEqualsFrom = !toDate.isBefore(from.right);
		if (!toAfterOrEqualsFrom) {
			context.buildConstraintViolationWithTemplate(MESSAGE_KEY_TO_BEFORE_FROM).addPropertyNode(TO_PROPERTY).addConstraintViolation();
		}

		final Long numberOfDays = from.right.until(toDate, ChronoUnit.DAYS);
		final boolean numberOfDaysInRange = numberOfDays <= dayLimit;
		if (!numberOfDaysInRange) {
			context.buildConstraintViolationWithTemplate(MESSAGE_KEY_DAY_LIMIT).addConstraintViolation();

		}

		if (!fromInFuture || !numberOfDaysInRange || !toAfterOrEqualsFrom) {
			return false;
		}

		localDateModel.setFromDate(from.right);
		localDateModel.setToDate(toDate);
		return true;
	}

}
