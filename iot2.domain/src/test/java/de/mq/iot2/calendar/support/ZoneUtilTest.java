package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class ZoneUtilTest {

	private static final LocalDate DATE_WINTER_TIME_OCTOBER = LocalDate.of(2023, 10, 31);
	private static final LocalDate DATE_SUMMER_TIME_OCTOBER = LocalDate.of(2023, 10, 3);
	private static final LocalDate DATE_SUMMER_TIME_MARCH = LocalDate.of(2023, 3, 31);
	private static final LocalDate DATE_WINTER_TIME_MARCH = LocalDate.of(2023, 3, 2);

	@Test
	void zoneOffsetEuropeanSummertimeHours() {
		assertEquals(1, ZoneUtil.zoneOffsetEuropeanSummertimeHours(DATE_WINTER_TIME_MARCH));
		assertEquals(2, ZoneUtil.zoneOffsetEuropeanSummertimeHours(DATE_SUMMER_TIME_MARCH));

		assertEquals(2, ZoneUtil.zoneOffsetEuropeanSummertimeHours(DATE_SUMMER_TIME_OCTOBER));
		assertEquals(1, ZoneUtil.zoneOffsetEuropeanSummertimeHours(DATE_WINTER_TIME_OCTOBER));
	}

	@Test
	void zoneOffsetEuropeanSummertimeHoursInvalid() {
		assertEquals(String.format(ZoneUtil.WRONG_ZONE_OFFSET_MESSAGE, 11 * 3600),
				assertThrows(IllegalArgumentException.class, () -> ZoneUtil.zoneOffsetEuropeanSummertimeHours(LocalDate.now(), ZoneId.of("Asia/Magadan"))).getMessage());
	}

	@Test
	void zoneOffsetHours() {
		assertEquals(0, ZoneUtil.zoneOffsetHours(DATE_WINTER_TIME_MARCH, ZoneId.of("Europe/London")));
		assertEquals(0, ZoneUtil.zoneOffsetHours(DATE_WINTER_TIME_OCTOBER, ZoneId.of("Europe/London")));
		assertEquals(1, ZoneUtil.zoneOffsetHours(DATE_SUMMER_TIME_MARCH, ZoneId.of("Europe/London")));
		assertEquals(1, ZoneUtil.zoneOffsetHours(DATE_SUMMER_TIME_OCTOBER, ZoneId.of("Europe/London")));
	}
	
	@Test
	void isEuropeanSummertime() {
		assertFalse(ZoneUtil.isEuropeanSummertime(DATE_WINTER_TIME_MARCH));
		assertFalse(ZoneUtil.isEuropeanSummertime(DATE_WINTER_TIME_OCTOBER));
		assertTrue(ZoneUtil.isEuropeanSummertime(DATE_SUMMER_TIME_MARCH));
		assertTrue(ZoneUtil.isEuropeanSummertime(DATE_SUMMER_TIME_OCTOBER));
	}


}
