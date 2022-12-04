package de.mq.iot2.configuration.support;



import de.mq.iot2.configuration.Configuration;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


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
