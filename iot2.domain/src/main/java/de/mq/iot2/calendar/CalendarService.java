package de.mq.iot2.calendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

public interface CalendarService {

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

	Optional<LocalTime> sunDownTime(final LocalDate date, final TwilightType twilightType);

	Optional<LocalTime> sunUpTime(final LocalDate date, final TwilightType twilightType);

	int addLocalDateDays(final String name, final LocalDate fromDate, final LocalDate toDate);

	int deleteLocalDateDays(final String name, final LocalDate fromDate, final LocalDate toDate);

	int deleteLocalDateDays(final int daysBack);

	Collection<DayGroup> dayGroups();

	Collection<Cycle> cycles();

	Collection<Day<?>> days(final DayGroup dayGroup);

	void deleteDay(final Day<?> day);

	Collection<DayOfWeek> unUsedDaysOfWeek();

	boolean createDayIfNotExists(final Day<?> day);

	void export(final OutputStream os);

	void importCsv(final InputStream os) throws IOException;

	void removecalendar();



}