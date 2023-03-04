package de.mq.iot2.configuration;

public interface Configuration {

	public enum RuleKey {
		EndOfDay, CleanUp
	}

	RuleKey key();

	String name();

}