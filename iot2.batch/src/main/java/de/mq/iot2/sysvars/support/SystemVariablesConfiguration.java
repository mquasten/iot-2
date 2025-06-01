package de.mq.iot2.sysvars.support;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
class SystemVariablesConfiguration {

	
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
	 

}
