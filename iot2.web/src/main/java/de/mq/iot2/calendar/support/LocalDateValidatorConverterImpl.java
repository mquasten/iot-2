package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.iot2.support.LocaleContextRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
class LocalDateValidatorConverterImpl implements ConstraintValidator<ValidLocalDateModel, LocalDateModel>{

	private final LocaleContextRepository localeContextRepository;
	private final long dayLimit; 
	
	LocalDateValidatorConverterImpl(final LocaleContextRepository localeContextRepository, @Value("${iot2.calendar.dayslimit:30}") final long dayLimit) {
		this.localeContextRepository = localeContextRepository;
		this.dayLimit=dayLimit;
	}


	
	private boolean isValid(final String value) {
		
		if( !StringUtils.hasText(value)) {
			return true;
		}
		final var locale = localeContextRepository.localeContext().getLocale();
		final var formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale); 
	
		try {
		LocalDate.parse(value, formatter);
		return true ; 
		} catch(Exception ex ) {
			return false;
		}
		
		
	}

	@Override
	public boolean isValid(final LocalDateModel localDateModel, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		localDateModel.setFromDate(null);
		localDateModel.setToDate(null);
		final boolean fromValid = isValid(localDateModel.getFrom());
		final boolean toValid = isValid(localDateModel.getTo());
		
		if ( ! fromValid ) {
			context.buildConstraintViolationWithTemplate("{error.date}").addPropertyNode("from").addConstraintViolation();	
		}
		
		if ( ! toValid ) {
			context.buildConstraintViolationWithTemplate("{error.date}").addPropertyNode("to").addConstraintViolation();	
		}
		

	    final boolean  fromAware = StringUtils.hasText(localDateModel.getFrom());
		if(! fromAware) {
			context.buildConstraintViolationWithTemplate("{error.mandatory}").addPropertyNode("from").addConstraintViolation();	 
		}
		
		if( !fromValid||!toValid||!fromAware) {
			return false;
		}
		
		final var locale = localeContextRepository.localeContext().getLocale();
		final DateTimeFormatter  formatter =   DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
	    final LocalDate from=  LocalDate.parse(localDateModel.getFrom(), formatter);
		final LocalDate to =  LocalDate.parse(StringUtils.hasText(localDateModel.getTo()) ? localDateModel.getTo() : localDateModel.getFrom(),formatter);
		
		final boolean fromInFuture = from.isAfter(LocalDate.now());
		if ( ! fromInFuture) {
			context.buildConstraintViolationWithTemplate("{error.date.future}").addPropertyNode("from").addConstraintViolation();
		} 
		
		final boolean toAfterOrEqualsFrom = !to.isBefore(from);
		if( !toAfterOrEqualsFrom) {
			context.buildConstraintViolationWithTemplate("{error.date.tobeforefrom}").addPropertyNode("to").addConstraintViolation();
		}
		
		final Long numberOfDays = from.until(to, ChronoUnit.DAYS);
		final boolean numberOfDaysInRange = numberOfDays<= dayLimit; 
		if(! numberOfDaysInRange ) {
			context.buildConstraintViolationWithTemplate("{error.date.limit}").addConstraintViolation();
		
		}
		
		
		if( !fromInFuture||!numberOfDaysInRange||!toAfterOrEqualsFrom) {
			return false;
		}
	
		localDateModel.setFromDate(from);
		localDateModel.setToDate(to);
		return true;
	}
	

	
	



	
}
