package de.mq.iot2.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.mq.iot2.configuration.Configuration.RuleKey;

class RuleKeyTest {
	
	@Test
	void values() {
		Arrays.asList(RuleKey.values()).forEach(ruleKey -> assertEquals(ruleKey, RuleKey.valueOf(ruleKey.name())));
	}

}
