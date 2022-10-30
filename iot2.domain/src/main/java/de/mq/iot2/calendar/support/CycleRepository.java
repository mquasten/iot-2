package de.mq.iot2.calendar.support;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.Cycle;

@RepositoryDefinition(domainClass = CycleImpl.class, idClass = String.class)
public interface CycleRepository {
	
	Cycle save(final Cycle cycle);

}
