package de.mq.iot2.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import javax.persistence.Id;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;



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
	

	public  static  String getId(final Object object) {
		final Collection<String> ids = new ArrayList<>();
		ReflectionUtils.doWithFields(object.getClass(), field-> {
			field.setAccessible(true);
			ids.add((String) ReflectionUtils.getField(field, object));
		}, field -> field.isAnnotationPresent(Id.class));
		return DataAccessUtils.requiredSingleResult(ids);
	}

	private static long randomLong() {
		final var random = new Random();
		return random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}
}
