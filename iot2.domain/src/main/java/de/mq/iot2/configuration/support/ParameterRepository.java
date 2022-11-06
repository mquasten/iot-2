package de.mq.iot2.configuration.support;

import java.util.Collection;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;

@RepositoryDefinition(domainClass = AbstractParameter.class, idClass = String.class)
public interface ParameterRepository {
	Collection<Parameter> findByConfiguration(final Configuration configuration);

	Parameter save(final Parameter parameter);

	void delete(final Parameter parameter);
}
