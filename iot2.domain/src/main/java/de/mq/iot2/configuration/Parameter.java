package de.mq.iot2.configuration;

import java.time.LocalTime;

import de.mq.iot2.calendar.CalendarService.TwilightType;

public interface Parameter {
	
	public enum Key {
		
		UpTime(LocalTime.class),
		MinSunDownTime(LocalTime.class),
		MaxSunDownTime(LocalTime.class),
		MinSunUpTime(LocalTime.class),
		MaxSunUpTime(LocalTime.class),
		SunUpDownType(TwilightType.class),
		ShadowTemperature(Double.class),
		DaysBack(Integer.class);
		
		private final Class<?> type;
		private Key(Class<?> type) {
			this.type=type;
		}
		
		public final Class<?> type() {
			return type;
		}
	}

	Key key();

	String value();

	Configuration configuration();

}