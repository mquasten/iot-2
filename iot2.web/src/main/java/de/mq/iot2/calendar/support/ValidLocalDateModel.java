package de.mq.iot2.calendar.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = DateValidatorImpl.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocalDateModel {

	String message() default "{error.date}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
