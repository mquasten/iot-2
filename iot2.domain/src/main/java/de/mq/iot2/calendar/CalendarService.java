package de.mq.iot2.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface CalendarService {
	public enum TimeType {
		Winter(1), Summer(2);

		private final int offset;

		TimeType(final int offset) {
			this.offset = offset;
		}

		public String key() {
			return name().toUpperCase();
		}

		public int offset() {
			return offset;
		}
	}

	public enum TwilightType {
		Mathematical(-50d / 60d), Civil(-6d), Nautical(-12d), Astronomical(-18d);

		private final double elevation;

		TwilightType(final double elevation) {
			this.elevation = elevation;
		}

		/**
		 * Elevation Horizont in Winkelgrad. Ist < 0.
		 * 
		 * @param elevation
		 */
		public final double horizonElevationInDegrees() {
			return elevation;
		}
	}

	void createDefaultCyclesGroupsAndDays();

	Cycle cycle(final LocalDate date);

	TimeType timeType(final LocalDate date);

	Optional<LocalTime> sunDownTime(final LocalDate date, final TwilightType twilightType);

	Optional<LocalTime> sunUpTime(final LocalDate date, final TwilightType twilightType);

	int addLocalDateDays(final String name, final LocalDate fromDate, final LocalDate toDate);

	int deleteLocalDateDays(final String name, final LocalDate fromDate, final LocalDate toDate);

	int deleteLocalDateDays(final int days);




}