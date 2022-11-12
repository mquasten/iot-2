package de.mq.iot2.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CalendarService {
	public enum  TimeType {
		Winter(1),
		Summer(2);
		
		private final int offset;
		TimeType(final int offset){
			this.offset=offset;
		}
		
		public String key() {
			return name().toUpperCase();
		}
		
		public int offset() {
			return offset;
		}
	}
	
	public enum TwilightType {
		Mathematical(-50d),
		Civil(-6d),
		Nautical(-12d),
		Astronomical(-18d);
		
		private final double elevation;
		
		TwilightType(final double elevation){
			this.elevation=elevation;
		}
		/**
		 * Elevation Horizont in Winkelminuten. Ist < 0.
		 * @param elevation
		 */
	    public final double horizonElevationInMinutesOfArc() {
	    	return elevation;
	    }
	}
	
	

	void createDefaultCyclesGroupsAndDays();
	
	Cycle cycle(final LocalDate date);

	TimeType timeType(final LocalDate date);

	LocalTime sunDownTime(final LocalDate date, final TwilightType twilightType);

	LocalTime sunUpTime(final LocalDate date, final TwilightType twilightType);

}