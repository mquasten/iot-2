package de.mq.iot2.support;

import java.util.Random;
import java.util.UUID;

import org.springframework.util.Assert;

public interface IdUtil {

	public static long string2Long(final String string) {
		Assert.notNull(string, "Value required.");
		final var uuid = UUID.nameUUIDFromBytes(string.getBytes());
		return uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
	}

	public static String id(final long id, final String discriminatorValue) {
		return new UUID(string2Long(discriminatorValue), id).toString();
	}

	public static String id(final long id) {
		return new UUID(id, id).toString();
	}

	public static String id() {
		return new UUID(randomLong(), System.currentTimeMillis()).toString();
	}

	private static long randomLong() {
		final var random = new Random();
		return random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}
}
