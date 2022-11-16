package de.mq.iot2.sysvars.support;



import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

@Repository
public class HomematicCCU2RepositoryImpl {
	
	private final RestOperations restOperations;
	
	final String url = "http://{host}:{port}/addons/xmlapi/{resource}";

	HomematicCCU2RepositoryImpl(RestOperations restOperations) {
		super();
		this.restOperations = restOperations;
	}
	
	

	

	
	public void readSystemVariables() {
		final var result = restOperations.getForObject(url, SystemVariables.class, Map.of("host", "homematic-ccu2", "port", "80", "resource", "sysvarlist.cgi"));
		 System.out.println(result.getSystemVariables().size());

	}

}
