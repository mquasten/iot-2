package de.mq.iot2.batch.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BatchConfiguration.class })
@Disabled
class RestTemplateTest {

	final String url = "http://{host}:{port}/addons/xmlapi/{resource}";

	@Autowired
	private RestOperations restOperations;

	@Test
	void restTemplate() {
		final var result = restOperations.getForObject(url, String.class, Map.of("host", "homematic-ccu2", "port", "80", "resource" , "version.cgi"));
		assertTrue(result.contains("<version>1.20</version>") );
				
	}

	

}
