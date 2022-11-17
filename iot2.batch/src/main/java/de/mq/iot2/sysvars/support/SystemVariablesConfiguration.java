package de.mq.iot2.sysvars.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource(value="classpath:/application.properties" ,ignoreResourceNotFound=true)
public class SystemVariablesConfiguration {

	@Bean
	RestOperations restOperations() {
		final var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		return new RestTemplate(clientHttpRequestFactory);
	}

}
