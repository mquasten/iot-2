package de.mq.iot2.protocol;

public interface SystemvariableProtocolProtocolParameter extends ProtocolParameter {
	public enum SystemvariableStatus {
		Calculated, Updated
	}

	void assignUpdated();

	SystemvariableStatus status();

}
