package de.mq.iot2.protocol.support;

import org.springframework.util.Assert;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.Protocol.Status;
import de.mq.iot2.protocol.ProtocolParameter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity(name = ProtocolParameterImpl.ENTITY_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PROTOCOL_PARAMETER_TYPE", length = 15)
@DiscriminatorValue("Parameter")
@IdClass(ProtocolParameterPrimaryKeyImpl.class)
class ProtocolParameterImpl implements ProtocolParameter {

	static final String MESSAGE_TYPE_IST_REQUIRED = "ProtocolParameterType ist required.";
	static final String MESSAGE_VALUE_IS_REQUIRED = "Value is required.";
	static final String MESSAGE_PROTOCOL_IS_REQUIRED = "Protocol is required.";
	static final String ENTITY_NAME = "ProtocolParameter";
	static final String MESSAGE_NAME_IS_REQUIRED = "Name is required.";

	@Id
	@Column(name = "NAME", length = 25, nullable = false)
	@Size(max = 25)
	@NotBlank
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "kind", length = 20, nullable = false)
	@NotNull
	private ProtocolParameterType type;

	@Column(name = "PARAMETER_VALUE", nullable = false, length = 50)
	@Size(max = 50)
	@NotBlank
	private String value;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATE", length = 15, nullable = true)
	private Status status;

	@Id
	@ManyToOne(targetEntity = ProtocolImpl.class)
	@JoinColumn(name = "PROTOCOL_ID", nullable = false)
	@NotNull
	@Valid
	private Protocol protocol;

	ProtocolParameterImpl() {

	}

	ProtocolParameterImpl(final Protocol protocol, final String name, final ProtocolParameterType type, final String value) {
		protocolRequiredGuard(protocol);
		nameRequiredGuard(name);
		typeRequiredGuard(type);
		valueRequiredGuard(value);
		this.protocol = protocol;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	private void typeRequiredGuard(final ProtocolParameterType type) {
		Assert.notNull(type, MESSAGE_TYPE_IST_REQUIRED);
	}

	private void nameRequiredGuard(final String name) {
		Assert.hasText(name, MESSAGE_NAME_IS_REQUIRED);
	}

	private void protocolRequiredGuard(final Protocol protocol) {
		Assert.notNull(protocol, MESSAGE_PROTOCOL_IS_REQUIRED);
	}

	private void valueRequiredGuard(final String value) {
		Assert.hasText(value, MESSAGE_VALUE_IS_REQUIRED);
	}

	@Override
	public String name() {
		nameRequiredGuard(name);
		return name;
	}

	@Override
	public ProtocolParameterType type() {
		typeRequiredGuard(type);
		return type;
	}

	@Override
	public String value() {
		valueRequiredGuard(value);
		return value;
	}

	@Override
	public Protocol protocol() {
		protocolRequiredGuard(protocol);
		return protocol;
	}

	private boolean missingKeyFields(final ProtocolParameterImpl protocolParameter) {
		return (protocolParameter.name == null) || (protocolParameter.protocol == null);
	}

	@Override
	public int hashCode() {
		if (missingKeyFields(this)) {
			return super.hashCode();
		}
		return name.hashCode() + protocol.hashCode();
	}

	@Override
	public boolean equals(final Object object) {

		if (!(object instanceof ProtocolParameter)) {
			return super.equals(object);

		}
		final var other = (ProtocolParameterImpl) object;

		if (missingKeyFields(this) || (missingKeyFields(other))) {
			return super.equals(object);
		}

		return other.name.equals(name) && other.protocol.equals(protocol);
	}

}
