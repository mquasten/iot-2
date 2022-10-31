package de.mq.iot2.calendar.support;

import java.util.Collection;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.Cycle;

@RepositoryDefinition(domainClass = CycleImpl.class, idClass = String.class)
public interface CycleRepository {
	
	Collection<Cycle>findByDefaultCycle(final boolean defaultCycle);
	Cycle save(final Cycle cycle);

}
