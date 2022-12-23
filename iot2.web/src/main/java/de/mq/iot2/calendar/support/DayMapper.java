package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.LocaleContextRepository;
import de.mq.iot2.support.ModelMapper;

@Component
class DayMapper implements ModelMapper<Day<?>, DayModel> {

	private final Map<Class<?>, BiFunction<Object, Locale, String>> valueConverters = Map.of(LocalDate.class,
			(date, locale) -> ((LocalDate) date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)), MonthDay.class,
			(date, locale) -> ((MonthDay) date).atYear(Year.now().getValue()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)), DayOfWeek.class,
			(day, locale) -> ((DayOfWeek) day).getDisplayName(TextStyle.SHORT_STANDALONE, locale));

	private final Map<Class<?>, Converter<Object, String>> sortedValueConverters = Map.of(LocalDate.class, date -> ((LocalDate) date).format(DateTimeFormatter.ofPattern("YYYYMMdd")), MonthDay.class,
			date -> ((MonthDay) date).atYear(Year.now().getValue()).format(DateTimeFormatter.ofPattern("YYYYMMdd")));

	private final LocaleContextRepository localeContextRepository;

	DayMapper(final LocaleContextRepository localeContextRepository) {
		this.localeContextRepository = localeContextRepository;
	}

	@Override
	public DayModel toWeb(final Day<?> day) {
		final var dayModel = new DayModel();
		dayModel.setId(IdUtil.getId(day));
		dayModel.setValue(value(day.value()));
		dayModel.setValueSorted(valueSorted(day.value()));
		day.description().ifPresent(dayModel::setDescription);
		return dayModel;
	}

	private String value(final Object value) {
		return valueConverters.containsKey(value.getClass()) ? valueConverters.get(value.getClass()).apply(value, localeContextRepository.localeContext().getLocale()) : value.toString();
	}

	private String valueSorted(final Object value) {
		return sortedValueConverters.containsKey(value.getClass()) ? sortedValueConverters.get(value.getClass()).convert(value) : value.toString();
	}

}
