package de.mq.iot2.configuration.support;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import de.mq.iot2.configuration.Configuration;


@Entity(name = "GlobalParameter")
@DiscriminatorValue("GlobalParameter")
class ParameterImpl extends AbstractParameter {

	@SuppressWarnings("unused")
	private ParameterImpl() {

	}

	ParameterImpl(final Configuration configuration, final Key key, final String value) {
		super(configuration, key, value);
	} 

	

}
