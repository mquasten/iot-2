package de.mq.iot2.configuration;

import java.time.LocalTime;

public interface Parameter {
	
	public enum Key {
		
		UpTime(LocalTime.class),
		MinSunDownTime(LocalTime.class),
		MaxSunUpTime(LocalTime.class),
		
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