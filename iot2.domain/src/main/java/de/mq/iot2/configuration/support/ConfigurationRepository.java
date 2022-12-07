package de.mq.iot2.configuration.support;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;

@RepositoryDefinition(domainClass = ConfigurationImpl.class, idClass = String.class)
public interface ConfigurationRepository {
	Collection<Configuration> findAll();
	
	Optional<Configuration> findByKey(final RuleKey key);
	
	Configuration save(final Configuration cycle);
}
