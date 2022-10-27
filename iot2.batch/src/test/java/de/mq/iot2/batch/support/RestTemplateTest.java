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

	final String url ="http://httpstat.us/200?sleep={sleep}";
	
	@Autowired
	private RestOperations restOperations;

	@Test
	void restTemplate() {
		assertTrue(restOperations.getForObject(url, String.class, Map.of("sleep", 100)).contains("\"description\":\"OK\""));
	}

}
