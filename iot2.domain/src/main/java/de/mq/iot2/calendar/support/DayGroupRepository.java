package de.mq.iot2.calendar.support;

import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.DayGroup;

@RepositoryDefinition(domainClass = DayGroupImpl.class, idClass = Long.class)
public interface DayGroupRepository {

	DayGroup save(final DayGroup dayGroup);

	Optional<DayGroup> findById(final String id);

}
