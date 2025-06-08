package de.mq.iot2.sysvars.support;

import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Aspect
@Component
public class UpdateSystemVariablesAspectImpl {
	static final String MAIL_SUBJECT = "Systemvariablen ccu2 aktualisiert";

	static final String SYSTEM_VARIABLES_MAIL_TEMPLATE = "systemVariables.html";

	static final String VARIABLE_NAME = "entries";

	private static Logger LOGGER = LoggerFactory.getLogger(UpdateSystemVariablesAspectImpl.class);

	private final JavaMailSender javaMailSender;
	private final ConfigurationService configurationService;
	private ISpringTemplateEngine templateEngine;
	
	private final Supplier<MimeMessageHelper> messageHelperSupplier;


	UpdateSystemVariablesAspectImpl(final JavaMailSender javaMailSender, final ConfigurationService configurationService, final ISpringTemplateEngine templateEngine, final Supplier<MimeMessageHelper> messageHelperSupplier) {
		this.javaMailSender = javaMailSender;
		this.configurationService = configurationService;
		this.templateEngine = templateEngine;
		this.messageHelperSupplier=messageHelperSupplier;
	}

	@AfterReturning(value = "execution(* de.mq.iot2.sysvars.support.SystemVariableServiceImpl.update(..) )", returning = "results")
	public void serviceAroundAdvice(final JoinPoint joinPoint, final Collection<SystemVariable> results) throws Throwable {

		final Optional<String> mailTo = configurationService.parameter(RuleKey.EndOfDay, Key.MailTo, String.class);
		if (mailTo.isEmpty()) {
			LOGGER.info("No mail reiceivers configured, no mail send.");
			return;
		}

		@SuppressWarnings("unchecked")
		final var systemvariables = (Collection<SystemVariable>) joinPoint.getArgs()[0];

		final var updated = results.stream().map(SystemVariable::getName).collect(Collectors.toSet());

		final var entries = systemvariables.stream().map(variable -> new AbstractMap.SimpleImmutableEntry<>(variable, updated.contains(variable.getName()))).collect(Collectors.toList());

		final var ctx = new Context(Locale.GERMANY);
		ctx.setVariable(VARIABLE_NAME, entries);
		final var text = templateEngine.process(SYSTEM_VARIABLES_MAIL_TEMPLATE, ctx);
		javaMailSender.send(mimeMessage(StringUtils.trim(mailTo.get()), text));

		LOGGER.info("Write systemvariables status mail to '{}'", mailTo.get());
	}

	private MimeMessage mimeMessage(final String mailTo , final String text  ) throws MessagingException, UnsupportedEncodingException {	
		final var  helper = messageHelperSupplier.get();
		helper.setTo(mailTo);
		helper.setSubject(MAIL_SUBJECT);
		helper.setText(text, true);
		return helper.getMimeMessage();
	}

}
