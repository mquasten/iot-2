package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariable;
import jakarta.mail.internet.MimeMessage;

class UpdateSystemVariablesAspectImplTest {
	private static final String MAIL_TEXT = "html";
	private final JavaMailSender javaMailSender = Mockito.mock(JavaMailSender.class);
	private final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private final ISpringTemplateEngine templateEngine = Mockito.mock(ISpringTemplateEngine.class);
	private final JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
	
	private final MimeMessageHelper messageHelper = Mockito.mock(MimeMessageHelper.class);

	private UpdateSystemVariablesAspectImpl updateSystemVariablesAspect = new UpdateSystemVariablesAspectImpl(javaMailSender, configurationService, templateEngine, ()-> messageHelper );

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

		final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
		final ArgumentCaptor<String> templateCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.when(templateEngine.process(templateCaptor.capture(), contextCaptor.capture())).thenReturn(MAIL_TEXT);
		Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		Mockito.when(messageHelper.getMimeMessage()).thenReturn(mimeMessage);
		updateSystemVariablesAspect.serviceAroundAdvice(joinPoint, updatedSystemVariables);

		Mockito.verify(javaMailSender).send(mimeMessage);

		assertEquals(UpdateSystemVariablesAspectImpl.SYSTEM_VARIABLES_MAIL_TEMPLATE, templateCaptor.getValue());

		assertTrue(contextCaptor.getValue().containsVariable(UpdateSystemVariablesAspectImpl.VARIABLE_NAME));
		@SuppressWarnings("unchecked")
		final Collection<Entry<SystemVariable, Boolean>> entries = (Collection<Entry<SystemVariable, Boolean>>) contextCaptor.getValue().getVariable(UpdateSystemVariablesAspectImpl.VARIABLE_NAME);

		final var results = entries.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		assertEquals(2, results.size());
		assertTrue(results.containsKey(systemVariable01));
		assertTrue(results.containsKey(systemVariable01));
		assertTrue(results.get(systemVariable01));
		assertFalse(results.get(systemVariable02));

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
	}


	
}