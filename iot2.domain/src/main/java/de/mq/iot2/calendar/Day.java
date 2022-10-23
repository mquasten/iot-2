package de.mq.iot2.calendar;

import java.time.LocalDate;
import java.util.Optional;

public interface Day<T> {
	
	boolean matches(final LocalDate date);
	
	T value();
	
	Optional<String> description();
	
	DayGroup dayGroup();

}
