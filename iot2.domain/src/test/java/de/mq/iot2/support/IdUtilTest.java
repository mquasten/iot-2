package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Id;

class IdUtilTest {

	private static final Long LONG = Long.valueOf(18310613);
	private static final String STRING = "James Clerk Maxwell";

	@Test
	void string2Long() {
		assertEquals(string2Long(STRING), IdUtil.string2Long(STRING));
	}

	private long string2Long(final String name) {
		return UUID.nameUUIDFromBytes(name.getBytes()).getMostSignificantBits()
				^ UUID.nameUUIDFromBytes(name.getBytes()).getLeastSignificantBits();
	}

	@Test
	void idLongAndDiscriminatorValue() {
		assertEquals(new UUID(string2Long(STRING), LONG).toString(), IdUtil.id(LONG, STRING));
	}

	@Test
	void idFromLong() {
		assertEquals(new UUID(LONG, LONG).toString(), IdUtil.id(LONG));
	}

	@Test
	void idRandomWithTimeStamp() {
		assertEquals(compareableDigitsFromTimestamp(new UUID(0, System.currentTimeMillis()).toString()),
				compareableDigitsFromTimestamp(IdUtil.id()));

	}

	private String compareableDigitsFromTimestamp(final String uuid) {
		final var values = uuid.split("[-]");
		final var last = values[values.length - 1];
		return values[values.length - 2] + "-" + last.substring(0, last.length() - 2);
	}

	@Test
	void getId() {
		final var id = RandomTestUtil.randomString();
		final var testEntity = new TestEntity(id);
		assertEquals(id, IdUtil.getId(testEntity));
	}

	@Test
	void assignId() {
		final var testEntity = new TestEntity(null);
		final var id = RandomTestUtil.randomString();
		IdUtil.assignId(testEntity, id);
		assertEquals(id, testEntity.id());
	}
}

class TestEntity {

	@Id
	private final String id;

	TestEntity(final String id) {
		this.id = id;
	}

	final String id() {
		return id;
	}
}
