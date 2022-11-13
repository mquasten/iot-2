package de.mq.iot2.calendar.support;

import java.time.LocalTime;
import java.util.Optional;

import de.mq.iot2.calendar.CalendarService.TwilightType;

class SunUpDownCalculatorImpl {
	private final double latitude;
	private final double longitude;
	private final double h;

	SunUpDownCalculatorImpl(final TwilightType twilightType) {
		this(51.1423399, 6.2815922, twilightType);
	}

	SunUpDownCalculatorImpl(final double latitudeDegrees, final double longitudeDegrees, final TwilightType twilightType) {
		this.latitude = latitudeDegrees * Math.PI / 180d;
		this.longitude = longitudeDegrees;
		h = twilightType.horizonElevationInDegrees() * Math.PI / 180d;
	}

	final Optional<LocalTime> sunUpTime(int dayOfYear, int timeZoneOffsetInHours) {

		return sunUpDown(dayOfYear, timeZoneOffsetInHours, false);
	}

	final Optional<LocalTime> sunDownTime(final int dayOfYear, final int timeZoneOffsetInHours) {

		return sunUpDown(dayOfYear, timeZoneOffsetInHours, true);
	}

	private Optional<LocalTime> sunUpDown(final int dayOfYear, final int timeZoneOffsetInHours, boolean isDown) {
		try {
			return Optional.of(localTime(time(dayOfYear, timeZoneOffsetInHours, isDown)));
		} catch (ArithmeticException exception) {
			return Optional.empty();
		}
	}

	private LocalTime localTime(final double result) {

		final int min = (int) Math.round(60 * (result % 1));

		if (result >= 24) {
			return LocalTime.of(((int) result) - 24, min);
		}

		if (min == 60) {
			return LocalTime.of(((int) result) + 1, 0);
		} else {
			return LocalTime.of((int) result, min);
		}
	}

	private double time(final int dayOfYear, final int timeZoneOffsetInHours, boolean isDown) {

		final double declination = 0.4095 * Math.sin(0.016906 * (dayOfYear - 80.086));

		final double timeDelta = 12 * acos((Math.sin(h) - Math.sin(latitude) * Math.sin(declination)) / (Math.cos(latitude) * Math.cos(declination))) / Math.PI;

		final double timeOffset = -0.171 * Math.sin(0.0337 * dayOfYear + 0.465) - 0.1299 * Math.sin(0.01787 * dayOfYear - 0.168);

		if (isDown) {
			return 12d + timeDelta - timeOffset - longitude / 15 + timeZoneOffsetInHours;

		} else {
			return 12d - timeDelta - timeOffset - longitude / 15 + timeZoneOffsetInHours;

		}

	}

	private double acos(final double x) {

		acosArgumentGuard(x);

		return Math.acos(x);
	}

	private void acosArgumentGuard(final double x) {
		if (Math.abs(x) > 1) {
			throw new ArithmeticException("Argument of acos should be in [-1,1].");
		}
	}

}
