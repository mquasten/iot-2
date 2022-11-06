package de.mq.iot2.calendar;

import java.util.UUID;

public interface Cycle {
	
	String name();
	
	int priority();

	boolean isDeaultCycle();

	UUID id();

}
