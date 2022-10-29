package de.mq.iot2.calendar.support;

import java.util.Collection;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

@RepositoryDefinition(domainClass = AbstractDay.class, idClass = String.class)
public interface DayRepository {
	Collection<Day<?>> findAll();

	Collection<Day<?>> findByDayGroup(final DayGroup dayGroup);

	Day<?> save(final Day<?> day);

	void delete(final Day<?> entity);

}
