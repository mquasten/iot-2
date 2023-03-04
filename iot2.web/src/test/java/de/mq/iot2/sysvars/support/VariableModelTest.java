package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.CalendarService.TwilightType;
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

	@ParameterizedTest
	@EnumSource(TwilightType.class)
	void twilightType(final TwilightType twilightType) {
		assertNull(variableModel.getTwilightType());

		variableModel.setTwilightType(twilightType.name());

		assertEquals(twilightType.name(), variableModel.getTwilightType());
	}

	@Test
	void sunUpToday() {
		assertNull(variableModel.getSunUpToday());

		final var time = randomTimeAsString();
		variableModel.setSunUpToday(time);

		assertEquals(time, variableModel.getSunUpToday());
	}

	private String randomTimeAsString() {
		final var random = new Random();

		return LocalTime.of(random.nextInt(0, 24), random.nextInt(0, 60)).format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	@Test
	void sunDownToday() {
		assertNull(variableModel.getSunDownToday());

		final var time = randomTimeAsString();
		variableModel.setSunDownToday(time);

		assertEquals(time, variableModel.getSunDownToday());
	}

	@Test
	void sunUpTomorrow() {
		assertNull(variableModel.getSunUpTomorrow());

		final var time = randomTimeAsString();
		variableModel.setSunUpTomorrow(time);

		assertEquals(time, variableModel.getSunUpTomorrow());
	}

	@Test
	void sunDownTomorrow() {
		assertNull(variableModel.getSunDownTomorrow());

		final var time = randomTimeAsString();
		variableModel.setSunDownTomorrow(time);

		assertEquals(time, variableModel.getSunDownTomorrow());
	}

	@Test
	void maxTemperatureToday() {
		assertNull(variableModel.getMaxTemperatureToday());

		final var temperature = randomTemperature();
		variableModel.setMaxTemperatureToday(temperature);

		assertEquals(temperature, variableModel.getMaxTemperatureToday());

	}

	private String randomTemperature() {
		return new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)).format(new Random().nextDouble(-10d, 40d));
	}

	@Test
	void maxTemperatureTomorrow() {
		assertNull(variableModel.getMaxTemperatureTomorrow());

		final var temperature = randomTemperature();
		variableModel.setMaxTemperatureTomorrow(temperature);

		assertEquals(temperature, variableModel.getMaxTemperatureTomorrow());
	}

}
