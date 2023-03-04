package de.mq.iot2.calendar.support;

import static de.mq.iot2.calendar.support.DayValidatorConverterImpl.MESSAGE_KEY_MANDATORY;
import static de.mq.iot2.calendar.support.DayValidatorConverterImpl.VALUE_FIELD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.MonthDay;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

class DayValidatorConverterImplTest {

	private final ConstraintValidator<ValidDayModel, DayModel> converter = new DayValidatorConverterImpl();

	private final ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);

	@ParameterizedTest
	@EnumSource
	void dayOfWeek(final DayOfWeek dayOfWeek) {

		final var dayModel = newDayOfWeekDayModel("" + dayOfWeek.getValue());

		assertTrue(converter.isValid(dayModel, constraintValidatorContext));

		assertEquals(dayOfWeek, dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
	}

	private DayModel newDayOfWeekDayModel(final String value) {
		final var dayModel = new DayModel();
		dayModel.setType(DayOfWeekDayImpl.class.getName());
		dayModel.setValue(value);
		dayModel.setTargetValue("targetValue");
		return dayModel;
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { " ", "" })
	void dayOfWeekMandatory(final String value) {
		final var dayModel = newDayOfWeekDayModel(value);
		final NodeBuilderCustomizableContext valueContext = mockConstraintValidatorContext(MESSAGE_KEY_MANDATORY);

		assertFalse(converter.isValid(dayModel, constraintValidatorContext));

		assertNull(dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(valueContext).addConstraintViolation();
	}

	private NodeBuilderCustomizableContext mockConstraintValidatorContext(final String key) {
		final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);
		final ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
		when(constraintValidatorContext.buildConstraintViolationWithTemplate(key)).thenReturn(constraintViolationBuilder);
		when(constraintViolationBuilder.addPropertyNode(VALUE_FIELD_NAME)).thenReturn(nodeBuilderCustomizableContext);
		return nodeBuilderCustomizableContext;
	}

	@Test
	void dayOfWeekInvalid() {
		final var dayModel = newDayOfWeekDayModel("0");
		final NodeBuilderCustomizableContext valueContext = mockConstraintValidatorContext(DayValidatorConverterImpl.MESSAGE_KEY_DAYOFWEEK_INVALID);

		assertFalse(converter.isValid(dayModel, constraintValidatorContext));

		assertNull(dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(valueContext).addConstraintViolation();
	}

	@ParameterizedTest
	@ValueSource(strings = { "13.06", "06/13" })
	void dayOfMonth(final String value) {

		final var dayModel = newDayOfMonthDayModel(value);

		assertTrue(converter.isValid(dayModel, constraintValidatorContext));

		assertEquals(MonthDay.of(6, 13), dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
	}

	private DayModel newDayOfMonthDayModel(final String value) {
		final var dayModel = new DayModel();
		dayModel.setType(DayOfMonthImpl.class.getName());
		dayModel.setValue(value);
		dayModel.setTargetValue("targetValue");
		return dayModel;
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { " ", "" })
	void dayOfMonthMandatory(final String value) {
		final var dayModel = newDayOfMonthDayModel(value);
		final NodeBuilderCustomizableContext valueContext = mockConstraintValidatorContext(MESSAGE_KEY_MANDATORY);

		assertFalse(converter.isValid(dayModel, constraintValidatorContext));

		assertNull(dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(valueContext).addConstraintViolation();
	}

	@ParameterizedTest
	@ValueSource(strings = { "13.06.1831", "06/13/1831" })
	void dayOfMonthWrongFormat(final String value) {
		final var dayModel = newDayOfMonthDayModel(value);
		final NodeBuilderCustomizableContext valueContext = mockConstraintValidatorContext(DayValidatorConverterImpl.MESSAGE_KEY_DAYOFMONTH_INVALID);

		assertFalse(converter.isValid(dayModel, constraintValidatorContext));

		assertNull(dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(valueContext).addConstraintViolation();
	}

	@ParameterizedTest
	@ValueSource(strings = { "30.02", "02/30" })
	void dayOfMonthParseError(final String value) {
		final var dayModel = newDayOfMonthDayModel(value);
		final NodeBuilderCustomizableContext valueContext = mockConstraintValidatorContext(DayValidatorConverterImpl.MESSAGE_KEY_DAYOFMONTH_INVALID);

		assertFalse(converter.isValid(dayModel, constraintValidatorContext));

		assertNull(dayModel.getTargetValue());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(valueContext).addConstraintViolation();
	}

}
