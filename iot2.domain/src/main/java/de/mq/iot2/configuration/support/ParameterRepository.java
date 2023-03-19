package de.mq.iot2.configuration.support;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = AbstractParameter.class, idClass = String.class)
public interface ParameterRepository {
	Optional<Parameter> findById(final String id);
	
	Collection<Parameter>findAll();

	Collection<Parameter> findByConfiguration(final Configuration configuration);

	@Query("select p  from GlobalParameter p where p.configuration.key= ?1 and p.key= ?2")
	Optional<Parameter> findByRuleKeyAndKey(final RuleKey ruleKey, final Key key);

	@Query("select p  from Parameter p where p.configuration.id= ?1 order by p.key")
	Collection<Parameter> findByConfigurationId(final String configurationId);

	Parameter save(@Valid Parameter parameter);

	@Modifying
	@Query("delete from Parameter p where configuration= ?1")
	void deleteByConfiguration(final Configuration configuration);
		
	@Modifying
	@Query("delete from Parameter")
	void deleteAll();
}
