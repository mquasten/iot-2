package de.mq.iot2.sysvars.support;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import jakarta.mail.MessagingException;

@Configuration
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
class SystemVariablesConfiguration {

	
	static final String EMAIL_TEMPLATE_SUFFIX = ".html";
	static final String EMAIL_TEMPLATE_PATH = "/email-templates/";
	
	static final String MAIL_PERSONAL = "iot-batch";
	
	private final String mailFrom; 
	

	SystemVariablesConfiguration( final @Value("${iot2.mail.from}")  String mailFrom) {
		this.mailFrom = mailFrom;
		
	}

	@Bean
	RestOperations restOperations() {
		final var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		return new RestTemplate(clientHttpRequestFactory);
	}
	
	
	@Bean
	JavaMailSender javaMailSender(@Value("${spring.mail.user}") final String user, @Value("${spring.mail.password}") final String password, @Value("#{${spring.mail.properties}}") final  Map<String,String> mailProperties)   {
		final var mailSender = new JavaMailSenderImpl();
		mailSender.setUsername(user);
		mailSender.setPassword(password);
		final var props = new Properties();
		props.putAll(mailProperties);
		mailSender.setJavaMailProperties(props); 
		return mailSender;
	} 
	
	@Bean
	  ISpringTemplateEngine emailTemplateEngine() {
	    final SpringTemplateEngine result = new SpringTemplateEngine();
	    result.addTemplateResolver(htmlTemplateResolver());
	    return result;
	  }

	  private ITemplateResolver htmlTemplateResolver() {
	    ClassLoaderTemplateResolver result = new ClassLoaderTemplateResolver();
	    result.setPrefix(EMAIL_TEMPLATE_PATH);
	    result.setSuffix(EMAIL_TEMPLATE_SUFFIX);
	    result.setTemplateMode(TemplateMode.HTML);
	    result.setCharacterEncoding(StandardCharsets.UTF_8.name());
	    return result;
	  }
	  

	  @Bean
	  Supplier<MimeMessageHelper> mimeMessageHelper(final JavaMailSender javaMailSender) throws MessagingException, UnsupportedEncodingException {
		    final var  mimeMessage = javaMailSender.createMimeMessage();
			final var  helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom(mailFrom, MAIL_PERSONAL);
			return () ->  helper;
	  }
		  
	  
	  
	 


	
}
