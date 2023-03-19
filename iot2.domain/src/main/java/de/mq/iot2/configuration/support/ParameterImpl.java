package de.mq.iot2.configuration.support;

import de.mq.iot2.configuration.Configuration;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = ParameterImpl.ENTITY_NAME)
@DiscriminatorValue(ParameterImpl.ENTITY_NAME)
class ParameterImpl extends AbstractParameter {

	static final String ENTITY_NAME = "GlobalParameter";

	@SuppressWarnings("unused")
	private ParameterImpl() {

	}

	ParameterImpl(final Configuration configuration, final Key key, final String value) {
		super(configuration, key, value);
	}

}
