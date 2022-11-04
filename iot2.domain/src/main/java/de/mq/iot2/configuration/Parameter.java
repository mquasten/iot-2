package de.mq.iot2.configuration;

public interface Parameter {
	
	public enum Key {
		
		UpTime,
		MinSunDownTime,
		MaxSunUpTime,
		
		DaysBack;
	}

	Key key();

	String value();

	Configuration configuration();

}