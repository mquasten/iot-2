package de.mq.iot2.calendar.support;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.DayGroup;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = DayGroupImpl.class, idClass = String.class)
public interface DayGroupRepository {

	DayGroup save(@Valid final DayGroup dayGroup);

	Optional<DayGroup> findById(final String id);

	Optional<DayGroup> findByName(final String name);

	Collection<DayGroup> findAll();
	
	@Modifying
	@Query("delete from DayGroup")
	void deleteAll();

}
