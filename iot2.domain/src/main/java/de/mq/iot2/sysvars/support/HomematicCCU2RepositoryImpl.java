package de.mq.iot2.sysvars.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariables;

@Repository
class HomematicCCU2RepositoryImpl implements SystemVariableRepository {

	static final String VALUE_REQUIRED_MESSAGE = "Value is required.";

	static final String ID_REQUIRED_MESSAGE = "Id is required.";

	static final String SYSTEM_VARIABLE_REQUIRED_MESSAGE = "SystemVariable is required.";

	static final String PARAMETER_VALUE = "value";

	static final String PARAMETER_ID = "id";

	static final String PARAMETER_PORT = "port";

	static final String RARAMETER_HOST = "host";

	private final RestOperations restOperations;

	final static String SYS_VAR_LIST_URL = "http://{host}:{port}/addons/xmlapi/sysvarlist.cgi";
	final static String STATE_CHANGE_URL = "http://{host}:{port}/addons/xmlapi/statechange.cgi?ise_id={id}&new_value={value}";

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
		Assert.notNull(systemVariable, SYSTEM_VARIABLE_REQUIRED_MESSAGE);
		Assert.hasText(systemVariable.getId(), ID_REQUIRED_MESSAGE);
		Assert.hasText(systemVariable.getValue(), VALUE_REQUIRED_MESSAGE);

		restOperations.put(STATE_CHANGE_URL, null, Map.of(RARAMETER_HOST, host, PARAMETER_PORT, port, PARAMETER_ID,
				systemVariable.getId(), PARAMETER_VALUE, systemVariable.getValue()));
	}
	

}
