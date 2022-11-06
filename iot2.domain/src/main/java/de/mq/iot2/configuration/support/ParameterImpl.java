package de.mq.iot2.configuration.support;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.util.Assert;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.support.IdUtil;

@Entity(name = "GlobalParameter")
@DiscriminatorValue("GlobalParameter")
@Table(name = "PARAMETER")
class ParameterImpl extends AbstractParameter {

	@SuppressWarnings("unused")
	private ParameterImpl() {

	}

	ParameterImpl(final Configuration configuration, final Key key, final String value) {
		super(id(configuration, key), configuration, key, value);
	}

	private static String id(final Configuration configuration, final Key key) {
		Assert.notNull(configuration, "Configuration is required.");
		Assert.notNull(key, "Key is required.");
		final var configurationId = configuration.id();
		return IdUtil.id(configurationId.getLeastSignificantBits() + configurationId.getMostSignificantBits(), key.name());
	}

}
