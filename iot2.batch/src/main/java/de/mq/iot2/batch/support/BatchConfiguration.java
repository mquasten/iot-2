package de.mq.iot2.batch.support;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

class BatchConfiguration {
	
	@Bean
	RestOperations restOperations() {
		return  new RestTemplate(getClientHttpRequestFactory());
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
	    final int timeout = 5000;
	    final var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
	    clientHttpRequestFactory.setConnectTimeout(timeout);
	    return clientHttpRequestFactory;
	}

}
