package de.mq.iot2.configuration.support;

import org.springframework.data.repository.RepositoryDefinition;
import de.mq.iot2.configuration.Configuration;

@RepositoryDefinition(domainClass = ConfigurationImpl.class, idClass = String.class)
public interface ConfigurationRepository {
	Configuration save(final Configuration cycle);
}
