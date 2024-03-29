package de.mq.iot2.configuration.support;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = ConfigurationImpl.class, idClass = String.class)
public interface ConfigurationRepository {
	Collection<Configuration> findAll();

	Optional<Configuration> findByKey(final RuleKey key);

	Configuration save(@Valid final Configuration cycle);
	
	@Modifying
	@Query("delete from Configuration")
	void deleteAll();
}
