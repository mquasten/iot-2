package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class ZoneUtilTest {

	@Test
	void zoneOffsetEuropeanSummertimeHours() {
		assertEquals(1, ZoneUtil.zoneOffsetEuropeanSummertimeHours(LocalDate.of(2023, 3, 2)));
		assertEquals(2, ZoneUtil.zoneOffsetEuropeanSummertimeHours(LocalDate.of(2023, 3, 31)));

		assertEquals(2, ZoneUtil.zoneOffsetEuropeanSummertimeHours(LocalDate.of(2023, 10, 3)));
		assertEquals(1, ZoneUtil.zoneOffsetEuropeanSummertimeHours(LocalDate.of(2023, 10, 31)));
	}

	@Test
	void zoneOffsetEuropeanSummertimeHoursInvalid() {
		assertEquals(String.format(ZoneUtil.WRONG_ZONE_OFFSET_MESSAGE, 11 * 3600),
				assertThrows(IllegalArgumentException.class, () -> ZoneUtil.zoneOffsetEuropeanSummertimeHours(LocalDate.now(), ZoneId.of("Asia/Magadan"))).getMessage());
	}

	@Test
	void zoneOffsetHours() {
		assertEquals(0, ZoneUtil.zoneOffsetHours(LocalDate.of(2023, 3, 2), ZoneId.of("Europe/London")));
	}

}
