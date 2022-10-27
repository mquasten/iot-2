package de.mq.iot2.batch.support;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

class BatchConfiguration {
	
	@Bean
	RestOperations restOperations() {
		 final var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		return  new RestTemplate(clientHttpRequestFactory);
	}

	

}
