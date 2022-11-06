package de.mq.iot2.configuration;

import java.util.UUID;

public interface Configuration {
	
	public enum RuleKey {
		EndOfDay,
		CleanUp
	}

	RuleKey key();

	String name();

	UUID id();

}