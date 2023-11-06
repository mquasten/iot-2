package de.mq.iot2.protocol;

public interface ProtocolParameter {

	public enum ProtocolParameterType {
		Input, Result, Configuration, IntermediateResult, RulesEngineArgument
	}

	String name();

	ProtocolParameterType type();

	String value();

	Protocol protocol();

}
