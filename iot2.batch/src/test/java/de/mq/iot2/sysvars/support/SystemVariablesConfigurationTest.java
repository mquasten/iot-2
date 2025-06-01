package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

class SystemVariablesConfigurationTest {

	private static final String PASSWORD  = UUID.randomUUID().toString();
	private static final String USER = UUID.randomUUID().toString();
	private final SystemVariablesConfiguration systemVariablesConfiguration = new SystemVariablesConfiguration();

	@Test
	void restOperations() {
		assertTrue(systemVariablesConfiguration.restOperations() instanceof RestTemplate);
	}

	@Test
	void javaMail() {
		final var map = Map.of("key1", "value1", "key2", "value2");
		final JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) systemVariablesConfiguration.javaMailSender(USER, PASSWORD,map);
		
		assertEquals(USER, javaMailSender.getUsername());
		assertEquals(PASSWORD, javaMailSender.getPassword());
		assertEquals( properties(map),  javaMailSender.getJavaMailProperties());
	}

	private Properties properties(final Map<String, String> map) {
		final var expected = new Properties();
		expected.putAll(map);
		return expected;
	}
}
