package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

class SystemVariablesConfigurationTest {

	private static final String MAIL_FROM = "mailFrom";
	private static final String PASSWORD  = UUID.randomUUID().toString();
	private static final String USER = UUID.randomUUID().toString();
	private final SystemVariablesConfiguration systemVariablesConfiguration = new SystemVariablesConfiguration(MAIL_FROM);

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
	
	@Test
	void  emailTemplateEngine() {
		final SpringTemplateEngine springTemplateEngine = (SpringTemplateEngine) systemVariablesConfiguration.emailTemplateEngine();
		assertEquals(1, springTemplateEngine.getTemplateResolvers().size());
		final ClassLoaderTemplateResolver resolver = (ClassLoaderTemplateResolver) springTemplateEngine.getTemplateResolvers().stream().findFirst().orElseThrow();
		assertEquals(SystemVariablesConfiguration.EMAIL_TEMPLATE_PATH, resolver.getPrefix());
		assertEquals(SystemVariablesConfiguration.EMAIL_TEMPLATE_SUFFIX, resolver.getSuffix());
		assertEquals(TemplateMode.HTML, resolver.getTemplateMode());
		assertEquals(StandardCharsets.UTF_8.name(), resolver.getCharacterEncoding());
	}
	
	@Test
	void mimeMessageHelper() throws UnsupportedEncodingException, MessagingException {
		final JavaMailSender javaMailSender=Mockito.mock(JavaMailSender.class);
		final MimeMessage message = Mockito.mock(MimeMessage.class);
		
		Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
		final var messageHelper = systemVariablesConfiguration.mimeMessageHelper(javaMailSender);
		assertEquals(message, messageHelper.get().getMimeMessage());
		Mockito.verify(message).setFrom(new InternetAddress(MAIL_FROM, SystemVariablesConfiguration.MAIL_PERSONAL));
	}
}
