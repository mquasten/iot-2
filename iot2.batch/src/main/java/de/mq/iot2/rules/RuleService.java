package de.mq.iot2.rules;

import java.util.Map;

import de.mq.iot2.configuration.Parameter.Key;

public interface RuleService {
	Map<String, Object> process(final Map<Key, Object> parameter, final Map<? extends Enum<?>, Object> arguments);

}