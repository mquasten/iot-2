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
	
	private final ConversionService conversionService;
	
	
	private final Map<Key, Predicate<String>> additionalValidations = Map.of(Key.DaysBack, value -> Integer.parseInt(value) > 0 );
	
	
	ParameterValidatorImpl(final ConversionService conversionService) {
		this.conversionService=conversionService;
	}
	
	
	
	
	

	@Override
	public boolean isValid(final ParameterModel value, final ConstraintValidatorContext context) {
		
		Assert.hasText(value.getName(), "Name is required.");
		
		final Key key = Key.valueOf(value.getName());
		
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(String.format("{parameter.value.%s.message}", key.type().getSimpleName().toLowerCase())).addPropertyNode("value").addConstraintViolation();
		try {
		
		   conversionService.convert(value.getValue(),key.type());
		   
		  return isValid(key, value.getValue());
				
		} catch(final ConversionFailedException ex )	{
			
		
			return false;
		}
		
	}

	private boolean isValid(final Key key, final String value) {
		
		if ( ! additionalValidations.containsKey(key)) {
			return true;
		}
		
		return additionalValidations.get(key).test(value);
		
	}

}
