package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import de.mq.iot2.configuration.Parameter.Key;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

class ParameterValidatorImplTest {

	private final ConstraintValidator<ValidParameter, ParameterModel> constraintValidator = new ParameterValidatorImpl(new ConfigurationBeans().conversionService());

	private final ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);

	private final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);

	NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);

	@Test
	void isValid() {
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.ShadowTemperature.name());
		parameterModel.setValue("25.0");

		assertTrue(constraintValidator.isValid(parameterModel, context));

		Mockito.verify(context).disableDefaultConstraintViolation();
		Mockito.verify(context, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
	}

	@Test
	void isValidWithoutAdditionalValidation() {
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.UpTime.name());
		parameterModel.setValue("5:30");

		assertTrue(constraintValidator.isValid(parameterModel, context));

		Mockito.verify(context).disableDefaultConstraintViolation();
		Mockito.verify(context, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
	}

	@Test
	void isValidAdditionalValidationFailed() {
		Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
		Mockito.when(constraintViolationBuilder.addPropertyNode("value")).thenReturn(nodeBuilderCustomizableContext);
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.ShadowTemperature.name());
		parameterModel.setValue("0.0");

		assertFalse(constraintValidator.isValid(parameterModel, context));

		Mockito.verify(context).disableDefaultConstraintViolation();
		Mockito.verify(context).buildConstraintViolationWithTemplate(String.format(ParameterValidatorImpl.MESSAGE_KEY, Key.ShadowTemperature.name().toLowerCase()));
		Mockito.verify(nodeBuilderCustomizableContext).addConstraintViolation();
	}

	@Test
	void isValidValidationFailed() {
		Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
		Mockito.when(constraintViolationBuilder.addPropertyNode("value")).thenReturn(nodeBuilderCustomizableContext);
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.UpTime.name());
		parameterModel.setValue("24:15");

		assertFalse(constraintValidator.isValid(parameterModel, context));

		Mockito.verify(context).disableDefaultConstraintViolation();
		Mockito.verify(context).buildConstraintViolationWithTemplate(String.format(ParameterValidatorImpl.MESSAGE_KEY, Key.UpTime.type().getSimpleName().toLowerCase()));
		Mockito.verify(nodeBuilderCustomizableContext).addConstraintViolation();
	}

	@Test
	void isValidRangeValidatior() {
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.DaysBack.name());
		parameterModel.setValue("30");

		assertTrue(constraintValidator.isValid(parameterModel, context));
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "1000" })
	void isValidRangeValidatiorFailed(final String value) {
		Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
		Mockito.when(constraintViolationBuilder.addPropertyNode("value")).thenReturn(nodeBuilderCustomizableContext);
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.DaysBack.name());
		parameterModel.setValue(value);

		assertFalse(constraintValidator.isValid(parameterModel, context));
	}
	
	@Test
	void isValidRangeValidatiorProtocolBack() {
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setName(Key.ProtocolBack.name());
		parameterModel.setValue("30");

		assertTrue(constraintValidator.isValid(parameterModel, context));
	}
	

}
