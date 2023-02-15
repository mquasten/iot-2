package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.sysvars.SystemVariable;

class VariableModelTest {

	private VariableModel variableModel = new VariableModel();

	@Test
	void date() {
		assertEquals(LocalDate.now(), variableModel.getDate());
	}
	@ParameterizedTest
	@MethodSource("locales")
	void today(Locale locale) {
		variableModel.setLocale(locale);
		assertEquals(dateString(locale, 0), variableModel.getToday());
	}
	
	@ParameterizedTest
	@MethodSource("locales")
	void tomorrow(Locale locale) {
		variableModel.setLocale(locale);
		assertEquals(dateString(locale, 1), variableModel.getTomorrow());
	}

	private String dateString(final Locale locale, final int offsetDays) {
		return LocalDate.now().plusDays(offsetDays).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale));
	}
	
	static Collection<Locale> locales() {
		return List.of(Locale.GERMAN, Locale.ENGLISH);
	}
	
	@Test
	void locale() {
		assertEquals(Locale.getDefault(), getLocale());
		
		variableModel.setLocale(Locale.CHINESE);
		
		assertEquals(Locale.CHINESE, getLocale());
	}
	private Object getLocale() {
		final var locale = ReflectionTestUtils.getField(variableModel, "locale");
		return locale;
	}
	
	@Test
	void variables() {
		assertEquals(0, variableModel.getVariables().size());
		
		final Collection<SystemVariable> variables = List.of(mock(SystemVariable.class));
		variableModel.setVariables(variables);
		
		assertEquals(variables, variableModel.getVariables());
	}
	
	@Test
	void showVariables() {
		assertFalse(variableModel.isShowVariables());
		
		variableModel.setShowVariables(true);
		
		assertTrue(variableModel.isShowVariables());
	}

}
