package de.mq.iot2.calendar.support;

import java.util.Collection;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.Day;

@RepositoryDefinition(domainClass = AbstractDay.class, idClass = String.class)
public interface DayRepository {
	Collection<Day<?>> findAll();

	Day<?> save(final Day<?> day);

}
