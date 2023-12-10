package de.mq.iot2.protocol.support;

import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus;

class ProtocolParameterModel {


	private String name;

	private ProtocolParameterType type;

	private String value;

	private SystemvariableStatus status;



	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public ProtocolParameterType getType() {
		return type;
	}

	public void setType(final ProtocolParameterType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public SystemvariableStatus getStatus() {
		return status;
	}

	public void setStatus(final SystemvariableStatus status) {
		this.status = status;
	}

	public boolean isSystemvariableParameter() {
		return status != null;
	}

}
