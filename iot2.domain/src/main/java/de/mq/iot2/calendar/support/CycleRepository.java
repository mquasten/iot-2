package de.mq.iot2.calendar.support;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.Cycle;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = CycleImpl.class, idClass = String.class)
public interface CycleRepository {
	Collection<Cycle>findAll();
	Collection<Cycle>findByDefaultCycle(final boolean defaultCycle);
	Cycle save(@Valid final Cycle cycle);
	
	Optional<Cycle> findById(final String id);

}
