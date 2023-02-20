package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.util.Assert;

public interface ZoneUtil {
	static final String WRONG_ZONE_OFFSET_MESSAHE = "Wrong zoneOffset: %s s.";
	public static  int  zoneOffsetHours(final LocalDate date, final ZoneId zoneId) {
		final var zoneOffsetSeconds   = ZonedDateTime.of(date, LocalTime.NOON ,zoneId).getOffset().getTotalSeconds();
		Assert.isTrue(zoneOffsetSeconds==3600||zoneOffsetSeconds==7200, String.format(WRONG_ZONE_OFFSET_MESSAHE,zoneOffsetSeconds));
		return zoneOffsetSeconds/3600;
	}

}
