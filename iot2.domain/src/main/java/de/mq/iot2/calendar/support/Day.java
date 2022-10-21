package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.util.Optional;

public interface Day<T> {
	
	boolean matches(LocalDate date);
	
	T value();
	
	Optional<String> description();

}
