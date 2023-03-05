package de.mq.iot2.calendar.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.DiscriminatorValue;

@Component
class DayCsvConverterImpl implements Converter<Pair<Day<?>, boolean[]>, String[]> {

	private static final String OUOTE = "\"";
	private final String csvDelimiter;
	private final Map<Class<?>, Function<AbstractDay<?>, String>> valueConverter = new HashMap<>();

	DayCsvConverterImpl(@Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
		valueConverter.put(GaussDayImpl.class, this::gaussDay);
	}

	private String gaussDay(final AbstractDay<?> day) {
		return "" + day.signum() * day.split(3)[0];
	}

	@Override
	public String[] convert(Pair<Day<?>, boolean[]> pair) {

		Assert.notNull(pair, "Value is required.");
		Assert.isTrue(pair.getSecond().length == 2, "2 flags required.");
		final var day = pair.getFirst();
		Assert.notNull(day, "Day is required.");
		Assert.notNull(pair.getSecond(), "Flags required.");
		final var dataGroupProcessed = pair.getSecond()[0];
		final var cycleProcessed = pair.getSecond()[1];
		System.out.println(dataGroupProcessed);
		System.out.println(cycleProcessed);
		final Collection<String> results = new ArrayList<>();
		addDay(day, results);
		return results.toArray(new String[results.size()]);
	}

	private void addDay(final Day<?> day, final Collection<String> results) {
		results.add(day.getClass().getAnnotation(DiscriminatorValue.class).value());
		Assert.isTrue(valueConverter.containsKey(day.getClass()), String.format("ValueConveter missing for class %s", day.getClass().getSimpleName()));
		results.add(valueConverter.get(day.getClass()).apply((AbstractDay<?>) day));
		results.add(quote(string(day.description())));
		results.add(id(day.dayGroup()));
	}

	private String string(Optional<String> value) {
		return value.orElse("");
	}

	private String id(Object entity) {
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

}
