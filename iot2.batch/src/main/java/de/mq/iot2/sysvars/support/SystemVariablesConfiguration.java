package de.mq.iot2.sysvars.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import de.mq.iot2.main.support.SimpleReflectionCommandLineRunner;

@Configuration
@ComponentScan(basePackages = SimpleReflectionCommandLineRunner.COMPONENT_SCAN_BASE_PACKAGE)
class SystemVariablesConfiguration {

	@Bean
	RestOperations restOperations() {
		final var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		return new RestTemplate(clientHttpRequestFactory);
	}

}
