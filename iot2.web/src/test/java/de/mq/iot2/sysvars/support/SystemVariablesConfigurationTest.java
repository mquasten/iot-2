package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class SystemVariablesConfigurationTest {

	private final SystemVariablesConfiguration systemVariablesConfiguration = new SystemVariablesConfiguration();

	@Test
	void restOperations() {
		assertTrue(systemVariablesConfiguration.restOperations() instanceof RestTemplate);
	}

}
