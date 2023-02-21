package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.util.Assert;

public interface ZoneUtil {
	static final ZoneId ZONE_ID_EUROPEAN_SUMMERTIME = ZoneId.of("Europe/Berlin");
	static final String WRONG_ZONE_OFFSET_MESSAGE = "Wrong zoneOffset: %s s.";

	public static int zoneOffsetEuropeanSummertimeHours(final LocalDate date) {
		return zoneOffsetEuropeanSummertimeHours(date, ZONE_ID_EUROPEAN_SUMMERTIME);
	}

	static int zoneOffsetEuropeanSummertimeHours(final LocalDate date, final ZoneId zoneId) {
		final var zoneOffsetSeconds = zoneOffsetSeconds(date, zoneId);
		Assert.isTrue(zoneOffsetSeconds == 3600 || zoneOffsetSeconds == 7200, String.format(WRONG_ZONE_OFFSET_MESSAGE, zoneOffsetSeconds));
		return zoneOffsetSeconds / 3600;
	}

	static int zoneOffsetHours(final LocalDate date, final ZoneId zoneId) {
		return zoneOffsetSeconds(date, zoneId) / 3600;
	}

	private static int zoneOffsetSeconds(final LocalDate date, final ZoneId zoneId) {
		return ZonedDateTime.of(date, LocalTime.NOON, zoneId).getOffset().getTotalSeconds();
	}

}
