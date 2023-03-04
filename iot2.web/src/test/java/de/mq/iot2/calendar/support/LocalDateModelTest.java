package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class LocalDateModelTest {

	private final LocalDateModel localDateModel = new LocalDateModel();

	@Test
	void from() {
		assertNull(localDateModel.getFrom());

		final var from = randomString();
		localDateModel.setFrom(from);

		assertEquals(from, localDateModel.getFrom());
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

	@Test
	void to() {
		assertNull(localDateModel.getTo());

		final var to = randomString();
		localDateModel.setTo(to);

		assertEquals(to, localDateModel.getTo());
	}

	@Test
	void dayGroupId() {
		assertNull(localDateModel.getDayGroupId());

		final var dayGroupId = randomString();
		localDateModel.setDayGroupId(dayGroupId);

		assertEquals(dayGroupId, localDateModel.getDayGroupId());
	}

	@Test
	void dayGroupName() {
		assertNull(localDateModel.getDayGroupName());

		final var dayGroupName = randomString();
		localDateModel.setDayGroupName(dayGroupName);

		assertEquals(dayGroupName, localDateModel.getDayGroupName());
	}

	@Test
	void fromDate() {
		assertThrows(IllegalArgumentException.class, () -> localDateModel.getFromDate());

		final var fromDate = LocalDate.now();
		localDateModel.setFromDate(fromDate);

		assertEquals(fromDate, localDateModel.getFromDate());
	}

	@Test
	void toDate() {
		assertThrows(IllegalArgumentException.class, () -> localDateModel.getToDate());

		final var toDate = LocalDate.now();
		localDateModel.setToDate(toDate);

		assertEquals(toDate, localDateModel.getToDate());
	}
}
