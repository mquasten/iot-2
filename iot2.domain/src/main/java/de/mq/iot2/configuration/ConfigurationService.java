package de.mq.iot2.configuration;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter.Key;

public interface ConfigurationService {

	void createDefaultConfigurationsAndParameters();

	Map<Key,Object> parameters(final RuleKey key, final Cycle cycle);

	<T> Optional<T> parameter(final RuleKey ruleKey, final Key key, final Class<T> clazz);

	Collection<Configuration> configurations();

	Collection<Parameter> parameters(final String configurationId);

	void save(final Parameter parmeter);

}