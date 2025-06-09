package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

class SystemVariablesConfigurationTest {

	private static final String MAIL_FROM = "mailFrom";
	private static final String PASSWORD = UUID.randomUUID().toString();
	private static final String USER = UUID.randomUUID().toString();
	private final SystemVariablesConfiguration systemVariablesConfiguration = new SystemVariablesConfiguration(MAIL_FROM);

	@Test
	void restOperations() {
		assertTrue(systemVariablesConfiguration.restOperations() instanceof RestTemplate);
	}

	@Test
	void javaMail() {
		final var map = Map.of("key1", "value1", "key2", "value2");
		final JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) systemVariablesConfiguration.javaMailSender(USER, PASSWORD, map);

		assertEquals(USER, javaMailSender.getUsername());
		assertEquals(PASSWORD, javaMailSender.getPassword());
		assertEquals(properties(map), javaMailSender.getJavaMailProperties());
	}

	private Properties properties(final Map<String, String> map) {
		final var expected = new Properties();
		expected.putAll(map);
		return expected;
	}

	@Test
	void emailTemplateEngine() throws VelocityException, IOException {
		final var velocityEngineFactoryBean = systemVariablesConfiguration.velocityEngineFactoryBean();

		@SuppressWarnings("unchecked")
		final List<String> loader = (List<String>) velocityEngineFactoryBean.createVelocityEngine().getProperty(SystemVariablesConfiguration.RESOURCE_LOADERS);
		assertEquals(1, loader.size());
		assertEquals(SystemVariablesConfiguration.RESOURCE_LOADER_CLASS, loader.get(0));

		@SuppressWarnings("unchecked")
		final List<String> classes = (List<String>) velocityEngineFactoryBean.createVelocityEngine().getProperty(SystemVariablesConfiguration.CLASS_RESOURCE_LOADER_CLASS);
		assertEquals(1, classes.size());
		assertEquals(ClasspathResourceLoader.class.getName(), classes.get(0));
	}

	@Test
	void mimeMessageHelper() throws UnsupportedEncodingException, MessagingException {
		final JavaMailSender javaMailSender = Mockito.mock(JavaMailSender.class);
		final MimeMessage message = Mockito.mock(MimeMessage.class);

		Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
		final var messageHelper = systemVariablesConfiguration.mimeMessageHelper(javaMailSender);
		assertEquals(message, messageHelper.get().getMimeMessage());
		Mockito.verify(message).setFrom(new InternetAddress(MAIL_FROM, SystemVariablesConfiguration.MAIL_PERSONAL));
	}
}
