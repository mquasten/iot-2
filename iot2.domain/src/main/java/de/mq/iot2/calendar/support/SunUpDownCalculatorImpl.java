package de.mq.iot2.calendar.support;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;


class SunUpDownCalculatorImpl {
	private final double latitude;

	private final double longitude;

	private final double h = -50d / 60d * Math.PI / 180d;
	// private final double h = -6d*Math.PI/180d;

	@Autowired
	SunUpDownCalculatorImpl() {
		this(51.1423399, 6.2815922);

	}

	private SunUpDownCalculatorImpl(final double latitudeDegrees, final double longitudeDegrees) {
		this.latitude = latitudeDegrees * Math.PI / 180d;
		this.longitude = longitudeDegrees;
	}

	// @Override
	public LocalTime sunUpTime(int dayOfYear, int timeZoneOffsetInHours) {

		return localTime(time(dayOfYear, timeZoneOffsetInHours, false));
	}

	// @Override
	public LocalTime sunDownTime(final int dayOfYear, final int timeZoneOffsetInHours) {
		return localTime(time(dayOfYear, timeZoneOffsetInHours, true));
	}

	private LocalTime localTime(final double result) {
		final int min = (int) Math.round(60 * (result % 1));

		return LocalTime.of((int) result, (min != 60) ? min : 59);
	}

	private double time(final int dayOfYear, final int timeZoneOffsetInHours, boolean isDown) {

		final double declination = 0.4095 * Math.sin(0.016906 * (dayOfYear - 80.086));

		final double timeDelta = 12 * Math.acos((Math.sin(h) - Math.sin(latitude) * Math.sin(declination)) / (Math.cos(latitude) * Math.cos(declination))) / Math.PI;

		final double timeOffset = -0.171 * Math.sin(0.0337 * dayOfYear + 0.465) - 0.1299 * Math.sin(0.01787 * dayOfYear - 0.168);

		if (isDown) {
			return 12d + timeDelta - timeOffset - longitude / 15 + timeZoneOffsetInHours;
		} else {

			return 12d - timeDelta - timeOffset - longitude / 15 + timeZoneOffsetInHours;
		}

	}

}
