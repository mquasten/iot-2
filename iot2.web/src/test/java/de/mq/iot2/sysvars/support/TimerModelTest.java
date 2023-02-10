package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

class TimerModelTest {

	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TimerController.TIME_PATTERN);
	private final TimerModel timerModel = new TimerModel();

	@Test
	void update() {
		assertFalse(timerModel.isUpdate());

		timerModel.setUpdate(true);

		assertTrue(timerModel.isUpdate());
	}

	@Test
	void UpTime() {
		assertNull(timerModel.getUpTime());

		final var time = randomTime();
		timerModel.setUpTime(time);

		assertEquals(time, timerModel.getUpTime());
	}

	private String randomTime() {
		/* randomTime in [00::00, 23:59] */
		return timeFormatter.format(LocalTime.ofNanoOfDay(Double.valueOf(Math.random() * LocalTime.of(23, 59).toNanoOfDay()).longValue()));
	}

	@Test
	void SunUpTime() {
		assertNull(timerModel.getSunUpTime());

		final var time = randomTime();
		timerModel.setSunUpTime(time);

		assertEquals(time, timerModel.getSunUpTime());
	}

	@Test
	void shadowTime() {
		assertNull(timerModel.getShadowTime());

		final var time = randomTime();
		timerModel.setShadowTime(time);

		assertEquals(time, timerModel.getShadowTime());
	}

	@Test
	void SunDownTime() {
		assertNull(timerModel.getSunDownTime());

		final var time = randomTime();
		timerModel.setSunDownTime(time);

		assertEquals(time, timerModel.getSunDownTime());
	}

}
