package de.mq.iot2.configuration;

import java.util.Map;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.Parameter.Key;

public interface ConfigurationService {

	void createDefaultConfigurationsAndParameters();

	Map<Key, ? extends Object> parameters(final RuleKey key, final Cycle cycle);

}