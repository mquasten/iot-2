package de.mq.iot2.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CalendarService {
	public enum  TimeType {
		Winter(1),
		Summer(2);
		
		private final int offset;
		TimeType(int offset){
			this.offset=offset;
		}
		
		public String key() {
			return name().toUpperCase();
		}
		
		public int offset() {
			return offset;
		}
	}

	void createDefaultCyclesGroupsAndDays();
	
	Cycle cycle(final LocalDate date);

	TimeType time(final LocalDate date);

	LocalTime sunDownTime(final LocalDate date);

	LocalTime sunUpTime(final LocalDate date);

}