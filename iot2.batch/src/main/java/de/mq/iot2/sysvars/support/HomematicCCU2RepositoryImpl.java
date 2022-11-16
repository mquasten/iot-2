package de.mq.iot2.sysvars.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

@Repository
class HomematicCCU2RepositoryImpl implements HomematicCCU2Repository {

	private static final String PARAMETER_VALUE = "value";

	private static final String PARAMETER_ID = "id";

	private static final String PARAMETER_PORT = "port";

	private static final String RARAMETER_HOST = "host";

	private final RestOperations restOperations;

	private final static String SYS_VAR_LIST_URL = "http://{host}:{port}/addons/xmlapi/sysvarlist.cgi";
	private final static String STATE_CHANGE_URL = "http://{host}:{port}/addons/xmlapi/statechange.cgi?ise_id={id}&new_value={value}";

	private final String host;
	private final Integer port;

	HomematicCCU2RepositoryImpl(final RestOperations restOperations, @Value("${iot2.ccu2.host}") final String host,
			@Value("${iot2.ccu2.port:80}") final Integer port) {
		this.restOperations = restOperations;
		this.host = host;
		this.port = port;
	}

	@Override
	public Collection<SystemVariable> readSystemVariables() {
		final var result = restOperations.getForObject(SYS_VAR_LIST_URL, SystemVariables.class,
				Map.of(RARAMETER_HOST, host, PARAMETER_PORT, port));

		return result.getSystemVariables();

	}

	@Override
	public void updateSystemVariable(final SystemVariable systemVariable) {
		Assert.notNull(systemVariable, "SystemVariable is required.");
		Assert.hasText(systemVariable.getId(), "Id is required.");
		Assert.hasText(systemVariable.getValue(), "Id is required.");

		restOperations.put(STATE_CHANGE_URL, null, Map.of(RARAMETER_HOST, host, PARAMETER_PORT, port, PARAMETER_ID,
				systemVariable.getId(), PARAMETER_VALUE, systemVariable.getValue()));
	}

}
