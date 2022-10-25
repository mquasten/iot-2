package de.mq.iot2.calendar;

import java.util.Collection;

public interface DayGroup {

	String name();

	boolean readOnly();

	void assign(Day<?> day);

	void remove(Day<?> day);

	Collection<Day<?>> days();

}
