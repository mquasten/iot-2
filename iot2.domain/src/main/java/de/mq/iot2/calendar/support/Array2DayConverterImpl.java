package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

@Component
class Array2DayConverterImpl implements Converter<Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>>, Day<?>> {

	static final String INVALID_VALUE_MESSAGE = "Invalid value: %s";
	static final String WRONG_NUMBER_OF_COLUMNS_MESSAGE = "10 columns expected.";
	private final ConversionService conversionService;

	private Map<String, BiFunction<DayGroup, String[], Day<?>>> days = Map.of(GaussDayImpl.ENTITY_NAME, this::gaussDay, DayOfMonthImpl.ENTITY_NAME, this::dayOfMonthDay,
			DayOfWeekDayImpl.ENTITY_NAME, this::dayOfWeekDay, LocalDateDayImp.ENTITY_NAME, this::localDateDay);

	Array2DayConverterImpl(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	private Day<?> gaussDay(final DayGroup dayGroup, final String[] columns) {
		return day(dayGroup, value(columns, 1, Integer.class), columns[2].strip(), GaussDayImpl.class);

	}

	private Day<?> day(final DayGroup dayGroup, final Object value, final String description, Class<?> type) {
		return description.isEmpty() ? (Day<?>) BeanUtils.instantiateClass(ReflectionUtils.findConstructor(type, dayGroup, value).orElseThrow(), dayGroup, value)
				: (Day<?>) BeanUtils.instantiateClass(ReflectionUtils.findConstructor(type, dayGroup, value, description).orElseThrow(), dayGroup, value, description);
	}

	private Day<?> dayOfMonthDay(final DayGroup dayGroup, final String[] columns) {
		final String valueString = value(columns, 1);
		final String values[] = valueString.split("[.]");
		Assert.isTrue(values.length == 2, String.format(INVALID_VALUE_MESSAGE, valueString));
		final MonthDay value = MonthDay.of(value(values[1], Integer.class), value(values[0], Integer.class));
		return day(dayGroup, value, columns[2].strip(), DayOfMonthImpl.class);
	}

	private Day<?> dayOfWeekDay(final DayGroup dayGroup, final String[] columns) {
		return day(dayGroup, DayOfWeek.of(value(value(columns, 1), Integer.class)), columns[2].strip(), DayOfWeekDayImpl.class);

	}

	private Day<?> localDateDay(final DayGroup dayGroup, final String[] columns) {
		final String valueString = value(columns, 1);
		final String values[] = valueString.split("[.]");
		Assert.isTrue(values.length == 3, String.format(INVALID_VALUE_MESSAGE, valueString));
		final LocalDate value = LocalDate.of(value(values[2], Integer.class), value(values[1], Integer.class), value(values[0], Integer.class));
		return day(dayGroup, value, columns[2].strip(), LocalDateDayImp.class);
	}

	@Override
	public Day<?> convert(final Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>> data) {
		Assert.notNull(data, "Input is required.");
		final String[] columns = data.getFirst();
		final Map<String, DayGroup> dataGroups = data.getSecond().getFirst();
		final Map<String, Cycle> cycles = data.getSecond().getSecond();
		Assert.isTrue(columns.length == 10, WRONG_NUMBER_OF_COLUMNS_MESSAGE);
		final Cycle cycle = cycle(columns, cycles);
		final DayGroup dayGroup = dayGroup(cycle, columns, dataGroups);
		return day(dayGroup, columns);
	}

	private Day<?> day(final DayGroup dayGroup, final String[] columns) {
		final String kind = value(columns, 0);
		Assert.isTrue(days.containsKey(kind), String.format("Day type  missing for %s.", kind));
		return days.get(kind).apply(dayGroup, columns);
	}

	private DayGroup dayGroup(final Cycle cycle, final String[] columns, final Map<String, DayGroup> dayGroups) {
		final long dayGroupId = value(columns, 3, Long.class);
		if (dayGroups.containsKey(IdUtil.id(dayGroupId))) {
			return dayGroups.get(IdUtil.id(dayGroupId));
		}
		final String name = value(columns, 4);
		final boolean readOnly = value(columns, 5, Boolean.class);
		return new DayGroupImpl(cycle, dayGroupId, name, readOnly);
	}

	private Cycle cycle(final String[] columns, final Map<String, Cycle> cycles) {
		final long cycleId = value(columns, 6, Long.class);
		if (cycles.containsKey(IdUtil.id(cycleId))) {
			return cycles.get(IdUtil.id(cycleId));
		}
		final String name = value(columns, 7);
		final int priority = value(columns, 8, Integer.class);
		final boolean defaultCycle = value(columns, 9, Boolean.class);

		return new CycleImpl(cycleId, name, priority, defaultCycle);
	}

	private <T> T value(final String[] columns, final int col, Class<T> type) {
		return value(value(columns, col), type);
	}

	private <T> T value(final String value, final Class<T> type) {
		return conversionService.convert(value, type);
	}

	private String value(final String[] columns, final int col) {
		final String result = columns[col].strip();
		Assert.hasText(result, String.format("Value reuired in Column %s", col));
		return result;
	}

}
