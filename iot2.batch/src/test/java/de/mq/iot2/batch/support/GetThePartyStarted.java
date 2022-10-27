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
class GetThePartyStarted {

	final String url ="http://httpstat.us/200";
	
	@Autowired
	private RestOperations restOperations;

	@Test
	void restTemplate() {
		assertTrue(restOperations.getForObject(url, String.class, Map.of("sleep", 0)).contains("\"description\":\"OK\""));
	}

}
