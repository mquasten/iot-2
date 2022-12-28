package de.mq.iot2.calendar.support;

import java.lang.reflect.Constructor;
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

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.LocaleContextRepository;
import de.mq.iot2.support.ModelMapper;
import jakarta.persistence.EntityNotFoundException;

@Component
class DayMapper implements ModelMapper<Day<?>, DayModel> {

	static final String DAY_NOT_FOUND_MESSAGE = "Day with id %s not found.";

	private final Map<Class<?>, BiFunction<Object, Locale, String>> valueConverters = Map.of(LocalDate.class,
			(date, locale) -> ((LocalDate) date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)), MonthDay.class,
			(date, locale) -> ((MonthDay) date).atYear(Year.now().getValue()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)).substring(0, 5),
			DayOfWeek.class, (day, locale) -> ((DayOfWeek) day).getDisplayName(TextStyle.SHORT_STANDALONE, locale));

	private final Map<Class<?>, Converter<Object, String>> sortedValueConverters = Map.of(LocalDate.class,
			date -> ((LocalDate) date).format(DateTimeFormatter.ofPattern("yyyyMMdd")), MonthDay.class,
			date -> ((MonthDay) date).atYear(Year.now().getValue()).format(DateTimeFormatter.ofPattern("yyyyMMdd")), DayOfWeek.class, day -> "" + ((DayOfWeek) day).name()

	);

	private final LocaleContextRepository localeContextRepository;

	private final DayRepository dayRepository;
	private final DayGroupRepository dayGroupRepository;

	DayMapper(final DayGroupRepository dayGroupRepository, final DayRepository dayRepository, final LocaleContextRepository localeContextRepository) {
		this.dayGroupRepository = dayGroupRepository;
		this.dayRepository = dayRepository;
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
		return valueConverters.containsKey(value.getClass()) ? valueConverters.get(value.getClass()).apply(value, localeContextRepository.localeContext().getLocale())
				: value.toString();
	}

	private String valueSorted(final Object value) {
		return sortedValueConverters.containsKey(value.getClass()) ? sortedValueConverters.get(value.getClass()).convert(value) : value.toString();
	}

	@Override
	public Day<?> toDomain(final String id) {
		return dayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(DAY_NOT_FOUND_MESSAGE, id)));

	}

	@Override
	public Day<?> toDomain(final DayModel dayModel) {
		Assert.notNull(dayModel, "DayModel is redired.");
		Assert.hasText(dayModel.getDayGroupId(), "DayGroup is required.");
		Assert.notNull(dayModel.getType(), "Type is required.");
		final DayGroup dayGroup = dayGroupRepository.findById(dayModel.getDayGroupId())
				.orElseThrow(() -> new EntityNotFoundException(String.format(CalendarServiceImp.DAY_GROUP_NOT_FOUND_MESSAGE, dayModel.getId())));
		final Class<?> targetEntity = dayModel.targetEntity();
		try {
			@SuppressWarnings("unchecked")
			final Constructor<Day<?>> c = (Constructor<Day<?>>) targetEntity.getDeclaredConstructor(DayGroup.class, dayModel.getTargetValue().getClass(), String.class);
			return BeanUtils.instantiateClass(c, new Object[] { dayGroup, dayModel.getTargetValue(), dayModel.getDescription() });

		} catch (final Exception ex) {
			throw new IllegalStateException(ex);
		}

	}

}
