package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class LocalTimeValidatorImplTest {
	
	private final ConstraintValidator<ValidTime, String> validator = new LocalTimeValidatorImpl();
	private final ConstraintValidatorContext context=  Mockito.mock(ConstraintValidatorContext.class);
	@Test
	void isValid() {
		assertTrue(validator.isValid("08:15", context));
	}
	
	@Test
	void isValidDelimiterMissing() {
		assertFalse(validator.isValid("12345", context));
	}
	
	@Test
	void isValidWrongLength() {
		assertFalse(validator.isValid("008:15", context));
	}
	
	@Test
	void isValidWrongTime() {
		assertFalse(validator.isValid("24:15", context));
	}
	
	@ParameterizedTest
	@NullSource
	@ValueSource(strings= {"", " "})
	void isValidEmpty(final String  value) {
		assertTrue(validator.isValid(value, context));
	}

}
