package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariable;
import jakarta.mail.internet.MimeMessage;

class UpdateSystemVariablesAspectImplTest {
	private static final String MAIL_TEXT = "html";
	private final JavaMailSender javaMailSender = Mockito.mock(JavaMailSender.class);
	private final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private final VelocityEngine velocityEngine = Mockito.mock(VelocityEngine.class);
	private final JoinPoint joinPoint = Mockito.mock(JoinPoint.class);

	private final MimeMessageHelper messageHelper = Mockito.mock(MimeMessageHelper.class);

	private UpdateSystemVariablesAspectImpl updateSystemVariablesAspect = new UpdateSystemVariablesAspectImpl(javaMailSender, configurationService, velocityEngine, () -> messageHelper);

	@Test
	void serviceAroundAdvice() throws Throwable {
		final SystemVariable systemVariable01 = new SystemVariable("Name01", "Wert01");
		final SystemVariable systemVariable02 = new SystemVariable("Name02", "Wert02");
		final Collection<SystemVariable> updatedSystemVariables = List.of(systemVariable01);
		final MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);

		final Collection<SystemVariable> systemVariables = List.of(systemVariable01, systemVariable02);
		Mockito.when(joinPoint.getArgs()).thenReturn(new Object[] { systemVariables });
		final String mailTo = "test@test.de";
		Mockito.when(configurationService.parameter(RuleKey.EndOfDay, Key.MailTo, String.class)).thenReturn(Optional.of(mailTo));

		Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		Mockito.when(messageHelper.getMimeMessage()).thenReturn(mimeMessage);

		Mockito.doAnswer(answer -> {
			answer.getArgument(3, StringWriter.class).write(MAIL_TEXT);
			return 	true;}
		).when(velocityEngine).mergeTemplate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		
		final ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> encodingCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Writer> writerCaptor = ArgumentCaptor.forClass(Writer.class);
		final ArgumentCaptor<VelocityContext> velocityContextCaptor = ArgumentCaptor.forClass(VelocityContext.class);

		updateSystemVariablesAspect.serviceAroundAdvice(joinPoint, updatedSystemVariables);
		
		Mockito.verify(velocityEngine).mergeTemplate( pathCaptor.capture(), encodingCaptor.capture(), velocityContextCaptor.capture(), writerCaptor.capture());
		assertEquals(UpdateSystemVariablesAspectImpl.TEMPLATE_PATH, pathCaptor.getValue());
		assertEquals(UpdateSystemVariablesAspectImpl.ENCODING, encodingCaptor.getValue());
		assertEquals(1, velocityContextCaptor.getValue().getKeys().length);
		assertEquals(UpdateSystemVariablesAspectImpl.VARIABLE_NAME, velocityContextCaptor.getValue().getKeys()[0]);

		@SuppressWarnings("unchecked")
		final Collection<Entry<SystemVariable, Boolean>> entries = (Collection<Entry<SystemVariable, Boolean>>) velocityContextCaptor.getValue().get(UpdateSystemVariablesAspectImpl.VARIABLE_NAME);
		final var results = entries.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(2, results.size());
		assertTrue(results.containsKey(systemVariable01));
		assertTrue(results.containsKey(systemVariable01));
		assertTrue(results.get(systemVariable01));
		assertFalse(results.get(systemVariable02));

		Mockito.verify(javaMailSender).send(mimeMessage);
		Mockito.verify(messageHelper).setTo(mailTo);
		Mockito.verify(messageHelper).setSubject(UpdateSystemVariablesAspectImpl.MAIL_SUBJECT);
		Mockito.verify(messageHelper).setText(MAIL_TEXT, true);
	}

	@Test
	void serviceAroundAdviceMailToEmpty() throws Throwable {

		final Collection<SystemVariable> updatedSystemVariables = List.of(new SystemVariable("Name01", "Wert01"));
		updateSystemVariablesAspect.serviceAroundAdvice(joinPoint, updatedSystemVariables);
		Mockito.verifyNoInteractions(joinPoint);
		Mockito.verifyNoInteractions(javaMailSender);
		Mockito.verifyNoInteractions(velocityEngine);
	}

}