package de.mq.iot2.configuration;

import de.mq.iot2.configuration.support.ParameterKey;

public interface Parameter {

	ParameterKey key();

	String value();

	Configuration configuration();

}