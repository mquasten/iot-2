package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus.Calculated;
import static de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus.Updated;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotNull;

@Entity(name = SystemvariableProtocolParameterImpl.ENTITY_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PROTOCOL_PARAMETER_TYPE", length = 15)
@DiscriminatorValue(SystemvariableProtocolParameterImpl.DISCRIMINATOR_VALUE)

class SystemvariableProtocolParameterImpl extends ProtocolParameterImpl implements SystemvariableProtocolParameter {
	static final String DISCRIMINATOR_VALUE = "Systemvariable";

	static final String MESSAGE_INVALID_STATUS = "Status should be 'Calculated'.";

	static final String MESSAGE_STATUS_REQUIRED = "Status is required.";

	static final String ENTITY_NAME = "SystemvariableProtocolParameter";
	final static String EMPTY_VALUE_STRING = "<Empty>";

	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 15, nullable = true)
	@NotNull
	private SystemvariableStatus status;

	SystemvariableProtocolParameterImpl() {
	}

	SystemvariableProtocolParameterImpl(final Protocol protocol, final String name, final String value) {
		super(protocol, name, Result,StringUtils.defaultIfBlank(StringUtils.defaultIfEmpty(value, EMPTY_VALUE_STRING), EMPTY_VALUE_STRING));
		this.status = Calculated;
	}
	
	SystemvariableProtocolParameterImpl(final Protocol protocol, final String name, final String value, final SystemvariableStatus status) {
		this(protocol, name, value);
		this.status=status;
	}

	@Override
	public void assignUpdated() {
		Assert.isTrue(this.status == Calculated, MESSAGE_INVALID_STATUS);
		this.status = Updated;
	}

	@Override
	public SystemvariableStatus status() {
		Assert.notNull(status, MESSAGE_STATUS_REQUIRED);
		return this.status;
	}

}
