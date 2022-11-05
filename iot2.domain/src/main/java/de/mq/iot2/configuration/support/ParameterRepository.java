package de.mq.iot2.configuration.support;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.transaction.annotation.Transactional;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;

@RepositoryDefinition(domainClass = AbstractParameter.class, idClass = String.class)
public interface ParameterRepository {
	Collection<Parameter> findByConfiguration(final Configuration configuration);
	Parameter save( Parameter parameter);
	
	 @Modifying
	 @Transactional
	void delete(final Parameter entity);
	
	  @Modifying
	  @Query("delete from Parameter p where p.configuration= ?1")
	  void deleteAll(Configuration configuration) ;
}
