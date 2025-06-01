package de.mq.iot2.sysvars.support;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.sysvars.SystemVariable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Aspect
@Component
public class UpdateSystemVariablesAspectImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(UpdateSystemVariablesAspectImpl.class);
	
	private final JavaMailSender javaMailSender;
	private final ConfigurationService configurationService;
	private final String mailFrom;
	UpdateSystemVariablesAspectImpl(final JavaMailSender javaMailSender, final ConfigurationService configurationService, @Value("${iot2.mail.from}") final String mailFrom){
		 this.javaMailSender= javaMailSender;
		 this.configurationService=configurationService;
		 this.mailFrom=mailFrom;
	}
	
	
	@AfterReturning(value ="execution(* de.mq.iot2.sysvars.support.SystemVariableServiceImpl.update(..) )", returning="results")
	public void serviceAroundAdvice( JoinPoint joinPoint , Collection<SystemVariable> results) throws Throwable  {
	
		final Optional<String> mailTo = configurationService.parameter(RuleKey.EndOfDay, Key.MailTo, String.class);
		if(mailTo.isEmpty()) {
			LOGGER.info("No mail reiceivers configured, no mail send.");
			return;
		}
		
		@SuppressWarnings("unchecked")
		final var systemvariables = (Collection<SystemVariable>) joinPoint.getArgs()[0];

		final var updated = results.stream().collect(Collectors.toMap(SystemVariable::getName, x -> x));
		
		final var writer = new StringWriter();
		writer.append("<html><h1>Systemvariablen ccu2 aktualisiert</h1><table border=1><tr><th>Variable</th><th>Wert</th><th>update</th></tr>");
		systemvariables.stream().forEach(variable -> writer.append(updated.containsKey(variable.getName()) ? toHtml(updated.get(variable.getName()), true) : toHtml(variable,false)));
		writer.append("</table></html>");
	   
	    javaMailSender .send(mimeMessage(StringUtils.trim(mailTo.get()), writer.getBuffer().substring(0)));
	        
		LOGGER.info("Write systemvariables status mail to '{}'", mailTo.get());
	}


	private MimeMessage mimeMessage(final String mailTo, final String text) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setFrom(mailFrom, "iot-batch");
		helper.setTo(mailTo);
		helper.setSubject("Update Systemvariablen ccu2");
		helper.setText(text, true);
		return message;
	}



	private String toHtml(final SystemVariable variable,final boolean updated) {
		return String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", variable.getName(), variable.getValue(), updated);
	}

}
