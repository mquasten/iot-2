package de.mq.iot2.calendar.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.DiscriminatorValue;

@Component
class DayCsvConverterImpl implements Converter<Pair<Day<?>, boolean[]>, String[]> {

	static final String BOOLEAN_ARRAY_WRONG_SIZE = "2 flags required.";
	private static final String OUOTE = "\"";
	private final String csvDelimiter;
	private final Map<Class<?>, Function<AbstractDay<?>, String>> valueConverter = new HashMap<>();

	DayCsvConverterImpl(@Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
		valueConverter.put(GaussDayImpl.class, this::gaussDay);
		valueConverter.put(DayOfMonthImpl.class, this::dayOfMonth);
		valueConverter.put(DayOfWeekDayImpl.class, this::dayOfWeek);
		valueConverter.put(LocalDateDayImp.class, this::localDateDay);
	}

	private String gaussDay(final AbstractDay<?> day) {
		return "" + day.signum() * day.split(3)[0];
	}

	private String dayOfMonth(final AbstractDay<?> day) {
		final int[] values = day.split(2);
		return numberValue(values[1], 2) + "." + numberValue(values[0], 2);
	}

	private String dayOfWeek(final AbstractDay<?> day) {
		return "" + day.split(1)[0];
	}

	private String localDateDay(final AbstractDay<?> day) {
		final int[] values = day.split(4, 2);
		return values[2] + "." + numberValue(values[1], 2) + "." + numberValue(values[0], 2);
	}

	@Override
	public String[] convert(Pair<Day<?>, boolean[]> pair) {

		Assert.notNull(pair, "Value is required.");
		Assert.isTrue(pair.getSecond().length == 2, BOOLEAN_ARRAY_WRONG_SIZE);
		final var day = pair.getFirst();
		Assert.notNull(day, "Day is required.");
		Assert.notNull(pair.getSecond(), "Flags required.");
		final var dataGroupProcessed = pair.getSecond()[0];
		final var cycleProcessed = pair.getSecond()[1];
		return Stream.concat(Stream.concat(day(day).stream(), dayGroup(day.dayGroup(), dataGroupProcessed)), cycle(day.dayGroup().cycle(), cycleProcessed))
				.toArray(size -> new String[size]);

	}

	private Stream<String> cycle(final Cycle cycle, final boolean processed) {
		if (processed) {
			return Stream.concat(Stream.of(id(cycle)), emptyColumns(3));
		}
		return Stream.of(id(cycle), quote(cycle.name()), String.valueOf(cycle.priority()), String.valueOf(cycle.isDeaultCycle()));
	}

	private Stream<String> dayGroup(final DayGroup dayGroup, final boolean processed) {
		if (processed) {
			return Stream.concat(Stream.of(id(dayGroup)), emptyColumns(2));

		}
		return Stream.of(id(dayGroup), quote(dayGroup.name()), String.valueOf(dayGroup.readOnly()));

	}

	private Collection<String> day(final Day<?> day) {
		Assert.isTrue(valueConverter.containsKey(day.getClass()), String.format("ValueConveter missing for class %s", day.getClass().getSimpleName()));
		return List.of(day.getClass().getAnnotation(DiscriminatorValue.class).value(), valueConverter.get(day.getClass()).apply((AbstractDay<?>) day),
				quote(string(day.description())));
	}

	private String string(final Optional<String> value) {
		return value.orElse("");
	}

	private Stream<String> emptyColumns(final int count) {
		return IntStream.range(0, count).mapToObj(i -> "");
	}

	private String id(final Object entity) {
		final UUID uuid = UUID.fromString(IdUtil.getId(entity));
		Assert.isTrue(uuid.getMostSignificantBits() == uuid.getLeastSignificantBits(), "Most and LeastSignificantBits should be the same.");
		return "" + uuid.getMostSignificantBits();
	}

	private String quote(final String text) {
		final var result = text.strip();
		if (text.contains(csvDelimiter)) {
			return OUOTE + result + OUOTE;
		}
		return result;
	}

	private String numberValue(final int value, int digits) {
		return String.format("%" + digits + "d", value).replace(' ', '0');
	}

}
