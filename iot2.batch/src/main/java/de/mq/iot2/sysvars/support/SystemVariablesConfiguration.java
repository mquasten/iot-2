package de.mq.iot2.sysvars.support;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.spring.VelocityEngineFactoryBean;
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

import jakarta.mail.MessagingException;

@Configuration
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
class SystemVariablesConfiguration {

	
	static final String RESOURCE_LOADER_CLASS = "class";
	 
	static final String CLASS_RESOURCE_LOADER_CLASS = "resource.loader.class.class";
	static final String RESOURCE_LOADERS = "resource.loaders";
	
	
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
	VelocityEngineFactoryBean velocityEngineFactoryBean() {
		final var velocityEngineFactoryBean = new VelocityEngineFactoryBean();
		
		final var  properties = new Properties();
		properties.putAll(Map.of(RESOURCE_LOADERS , RESOURCE_LOADER_CLASS, CLASS_RESOURCE_LOADER_CLASS , List.of(ClasspathResourceLoader.class.getName())));
		velocityEngineFactoryBean.setVelocityProperties(properties);
		return velocityEngineFactoryBean;
	}
	
	  @Bean
	  Supplier<MimeMessageHelper> mimeMessageHelper(final JavaMailSender javaMailSender) throws MessagingException, UnsupportedEncodingException {
		    final var  mimeMessage = javaMailSender.createMimeMessage();
			final var  helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom(mailFrom, MAIL_PERSONAL);
			return () ->  helper;
	  }
		  
	  
	  
	 


	
}
