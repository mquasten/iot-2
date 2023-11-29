package de.mq.iot2.protocol.support;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.protocol.Protocol;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = ProtocolImpl.class, idClass = String.class)
interface ProtocolRepository {
	Protocol save(@Valid final Protocol protocol);
	Collection<Protocol>findByExecutionTimeBefore(final LocalDateTime executionTime);
	void delete(final Protocol protocol);
	
	@Query("select distinct name from Protocol order by name")
	Collection<String> findDistinctNames() ;
	
	Collection<Protocol>findByNameOrderByExecutionTime(final String name );
	
	
	
	
}
