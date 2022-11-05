package de.mq.iot2.configuration.support;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import de.mq.iot2.configuration.Configuration;


@Entity(name = "GlobalParameter")
@DiscriminatorValue("GlobalParameter")
@Table(name="PARAMETER")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
class ParameterImpl extends AbstractParameter{
	
	@SuppressWarnings("unused")
	private ParameterImpl() {
		
	}
	
	ParameterImpl(final Configuration configuration, final Key key, final String value) {
		super(configuration,key, value);
	}
	
	


}
