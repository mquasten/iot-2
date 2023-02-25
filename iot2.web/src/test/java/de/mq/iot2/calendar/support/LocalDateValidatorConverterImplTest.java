package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

class LocalDateValidatorConverterImplTest {

	private static final String INVALID_DATE = "xx.xx.xxxx";

	private final ConstraintValidator<ValidLocalDateModel, LocalDateModel> converter = new LocalDateValidatorConverterImpl(30);

	private final ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
	private final NodeBuilderCustomizableContext fromContext = mock(NodeBuilderCustomizableContext.class);
	private final NodeBuilderCustomizableContext toContext = mock(NodeBuilderCustomizableContext.class);
	private final ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);

	@BeforeEach
	void mockConstraintValidatorContextForProperty() {

		
		when(constraintValidatorContext.buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_INVALID_DATE)).thenReturn(constraintViolationBuilder);
		when(constraintViolationBuilder.addPropertyNode(LocalDateValidatorConverterImpl.FROM_PROPERY)).thenReturn(fromContext);
		when(constraintViolationBuilder.addPropertyNode(LocalDateValidatorConverterImpl.TO_PROPERTY)).thenReturn(toContext);
		when(constraintValidatorContext.buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_MANDATORY)).thenReturn(constraintViolationBuilder);
		when(constraintValidatorContext.buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_DATE_FUTURE)).thenReturn(constraintViolationBuilder);
		when(constraintValidatorContext.buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_TO_BEFORE_FROM)).thenReturn(constraintViolationBuilder);
		when(constraintValidatorContext.buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_DAY_LIMIT)).thenReturn(constraintViolationBuilder);
		
	}

	@Test
	void isValidGerman() {
		final LocalDate date = LocalDate.now();

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(stringGerman(date.plusDays(1)));
		localDateModel.setTo(stringGerman(date.plusDays(2)));

		assertTrue(converter.isValid(localDateModel, constraintValidatorContext));

		assertEquals(date.plusDays(1), localDateModel.getFromDate());
		assertEquals(date.plusDays(2), localDateModel.getToDate());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();

	}

	@Test
	void isValidEnglish() {
		final LocalDate date = LocalDate.now();

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(stringEnglish(date.plusDays(1)));
		localDateModel.setTo(stringEnglish(date.plusDays(2)));

		assertTrue(converter.isValid(localDateModel, constraintValidatorContext));

		assertEquals(date.plusDays(1), localDateModel.getFromDate());
		assertEquals(date.plusDays(2), localDateModel.getToDate());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();

	}

	@Test
	void isValidGermanInvalidFormat() {

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom("01.01");
		localDateModel.setTo("01.01");
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
		verify(toContext).addConstraintViolation();

	}

	private void assertFromDateAndToDateAreNull(final LocalDateModel localDateModel) {
		assertNull(ReflectionTestUtils.getField(localDateModel, "fromDate"));
		assertNull(ReflectionTestUtils.getField(localDateModel, "toDate"));
	}

	private String stringGerman(final LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
	}

	private String stringEnglish(final LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	@Test
	void isValidEnglishInvalidFormat() {

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom("01/01");
		localDateModel.setTo("01/01");
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
		verify(toContext).addConstraintViolation();

	}

	@Test
	void isValidGermanInvalidDate() {

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom("30.02.2099");
		localDateModel.setTo("30.02.2099");
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
		verify(toContext).addConstraintViolation();

	}

	@Test
	void isValidEnglishInvalidDate() {

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom("02/30/2099");
		localDateModel.setTo("02/30/2099");
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
		verify(toContext).addConstraintViolation();

	}

	@Test
	void isValidToDateMissing() {
		final LocalDate date = LocalDate.now();

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(stringGerman(date.plusDays(1)));

		assertTrue(converter.isValid(localDateModel, constraintValidatorContext));

		assertEquals(date.plusDays(1), localDateModel.getFromDate());
		assertEquals(date.plusDays(1), localDateModel.getToDate());
		verify(constraintValidatorContext).disableDefaultConstraintViolation();

	}

	@Test
	void isValidfromDateMissing() {
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
		verify(constraintValidatorContext).buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_MANDATORY);
	}

	@Test
	void isValidFromNotInFuture() {
		final LocalDate date = LocalDate.now();
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(stringGerman(date));
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
		verify(constraintValidatorContext).buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_DATE_FUTURE);
	}

	@Test
	void isValidGermanToBeforeFrom() {
		final LocalDate date = LocalDate.now();

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(stringEnglish(date.plusDays(2)));
		localDateModel.setTo(stringEnglish(date.plusDays(1)));
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(toContext).addConstraintViolation();
		verify(constraintValidatorContext).buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_TO_BEFORE_FROM);

	}

	@Test
	void isValidGermanDayLimit() {
		final LocalDate date = LocalDate.now();

		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(stringGerman(date.plusDays(1)));
		localDateModel.setTo(stringGerman(date.plusDays(32)));
		localDateModel.setFromDate(LocalDate.now());
		localDateModel.setToDate(LocalDate.now());

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		assertFromDateAndToDateAreNull(localDateModel);
		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(constraintViolationBuilder).addConstraintViolation();
		verify(constraintValidatorContext).buildConstraintViolationWithTemplate(LocalDateValidatorConverterImpl.MESSAGE_KEY_DAY_LIMIT);

	}

	@Test
	void isValidFromInvalid() {
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setFrom(INVALID_DATE);

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(fromContext).addConstraintViolation();
	}

	@Test
	void isValidToInvalid() {
		final LocalDateModel localDateModel = new LocalDateModel();
		localDateModel.setTo(INVALID_DATE);
		localDateModel.setFrom(stringGerman(LocalDate.now().plusDays(1)));

		assertFalse(converter.isValid(localDateModel, constraintValidatorContext));

		verify(constraintValidatorContext).disableDefaultConstraintViolation();
		verify(toContext).addConstraintViolation();
	}

}
