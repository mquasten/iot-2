package de.mq.iot2.main.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
@Inherited
public @interface BatchMethod {

	String value();
	Class<? extends Converter<List<String>, Object[]>> converterClass() default NoArgumentConverter.class;

}
