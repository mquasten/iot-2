package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static de.mq.iot2.protocol.SystemvariableProtocolProtocolParameter.SystemvariableStatus.Calculated;
import static de.mq.iot2.protocol.SystemvariableProtocolProtocolParameter.SystemvariableStatus.Updated;

import org.springframework.util.Assert;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.SystemvariableProtocolProtocolParameter;
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
@DiscriminatorValue(SystemvariableProtocolParameterImpl.ENTITY_NAME)

class SystemvariableProtocolParameterImpl  extends ProtocolParameterImpl implements SystemvariableProtocolProtocolParameter {
	static final String ENTITY_NAME = "SystemvariableProtocolParameter";
	
	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 15, nullable = false)
	@NotNull
	private SystemvariableStatus status;
	
	SystemvariableProtocolParameterImpl(){
	}
	
	SystemvariableProtocolParameterImpl(final Protocol protocol, final String name, final String value) {
		super(protocol, name, Result, value);
		this.status=Calculated;
	}
	
	@Override
	public void assignUpdated() {
		Assert.isTrue(this.status==Calculated, "Status should be 'Calculated'.");
		this.status=Updated;
	}
	@Override
	public SystemvariableStatus status() {
		return this.status;
	}

}