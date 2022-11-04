package de.mq.iot2.configuration.support;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.IdUtil;



@Entity(name = "Parameter")
@Table(name="Parameter")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "PARAMETER_TYPE", length = 15)
class ParameterImpl implements Parameter {
	
	@Id
	@Column(name = "ID", length = 36, nullable = false)
	private String id;
	
	@Column(name = "PARAMETER_KEY", nullable = false)
	@Enumerated(EnumType.STRING)
	private Key key;
	@Column(name = "PARAMETER_VALUE", nullable = false)
	private String value;
	
	
	@ManyToOne(targetEntity = ConfigurationImpl.class)
	@JoinColumn(name = "CONFIGURATION_ID", nullable = false)
	private Configuration configuration;
	
	
	@SuppressWarnings("unused")
	private ParameterImpl() {
		
	}
	
	ParameterImpl(final Configuration configuration,  final Key key, final String value) {
		configurationRequiredGuard(configuration);
		keyRequiredGuard(key);
		valueRequiredGuard(value);
		id=IdUtil.id();
		this.configuration=configuration;
		this.key=key;
		this.value=value;
	}

	private void valueRequiredGuard(final String value) {
		Assert.hasText(value, "Value is required.");
	}

	private void keyRequiredGuard(final Key key) {
		Assert.notNull(key, "Key is required.");
	}

	private void configurationRequiredGuard(final Configuration configuration) {
		Assert.notNull(configuration, "Configuration is required.");
	}
	
	@Override
	public Key key() {
		keyRequiredGuard(key);
		return key;
	}
	
	@Override
	public String value() {
		valueRequiredGuard(value);
		return value;
	}
	
	@Override
	public Configuration configuration() {
		configurationRequiredGuard(configuration);
		return configuration;
	}
	

}
