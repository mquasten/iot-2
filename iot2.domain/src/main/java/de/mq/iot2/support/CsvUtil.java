package de.mq.iot2.support;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.util.Assert;

public interface CsvUtil {
	
	static final String WRONG_ID_MESSAGE = "Most and LeastSignificantBits should be the same.";
	static final String OUOTE = "\"";
	
	public static String string(final Optional<String> value) {
		return value.orElse("");
	}

	public static Stream<String> emptyColumns(final int count) {
		return IntStream.range(0, count).mapToObj(_ -> "");
	}

	public static String id(final Object entity) {
		final UUID uuid = UUID.fromString(IdUtil.getId(entity));
		Assert.isTrue(uuid.getMostSignificantBits() == uuid.getLeastSignificantBits(), WRONG_ID_MESSAGE);
		return "" + uuid.getMostSignificantBits();
	}

	public static String quote(final String text, final String delimiter) {
		final var result = text.strip();
		if (text.contains(delimiter)) {
			return OUOTE + result + OUOTE;
		}
		return result;
	}

}
