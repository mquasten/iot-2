package de.mq.iot2.sysvars.support;

import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.ID_REQUIRED_MESSAGE;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.PARAMETER_ID;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.PARAMETER_PORT;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.PARAMETER_VALUE;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.RARAMETER_HOST;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.STATE_CHANGE_URL;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.SYSTEM_VARIABLE_REQUIRED_MESSAGE;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.SYS_VAR_LIST_URL;
import static de.mq.iot2.sysvars.support.HomematicCCU2RepositoryImpl.VALUE_REQUIRED_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestOperations;

import de.mq.iot2.support.RandomTestUtil;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariables;

class HomematicCCU2RepositoryImplTest {
	private static final String HOST = RandomTestUtil.randomString();
	private static final Integer PORT = RandomTestUtil.randomInt();
	
	private final RestOperations restOperations = mock(RestOperations.class);
	
	private final HomematicCCU2Repository homematicCCU2Repository = new HomematicCCU2RepositoryImpl(restOperations, HOST, PORT);
	

	@SuppressWarnings("unchecked")
	@Test
	void readSystemVariables() {
		
		final var systemVariables =   new SystemVariables();
		systemVariables.setSystemVariables(List.of(new SystemVariable(), new SystemVariable()));
	
		when(restOperations.getForObject(argThat(arg -> SYS_VAR_LIST_URL.equals(arg)), argThat(arg -> SystemVariables.class.equals(arg)), (Map<String,?>) argThat(  arg -> Map.of(RARAMETER_HOST, HOST, PARAMETER_PORT, PORT ).equals(arg)) )).thenReturn(systemVariables);
	
		assertEquals( systemVariables.getSystemVariables(), homematicCCU2Repository.readSystemVariables());
	}
	@SuppressWarnings("unchecked")
	@Test
	void updateSystemVariable() {
		final var systemVariable = new SystemVariable();
		systemVariable.setId("" + RandomTestUtil.randomInt());
		systemVariable.setValue(RandomTestUtil.randomString());
		
		homematicCCU2Repository.updateSystemVariable(systemVariable);
		
		verify(restOperations).put(argThat(arg -> STATE_CHANGE_URL.equals(arg)), argThat(arg -> arg==null), (Map<String,?>) argThat(arg -> Map.of(RARAMETER_HOST, HOST, PARAMETER_PORT, PORT, PARAMETER_ID, systemVariable.getId(), PARAMETER_VALUE, systemVariable.getValue() ).equals(arg)));
	}
	

	@Test
	void updateSystemVariableNull() {
		assertEquals(SYSTEM_VARIABLE_REQUIRED_MESSAGE, assertThrows(IllegalArgumentException.class, () -> homematicCCU2Repository.updateSystemVariable(null)).getMessage());
	}
	
	@Test
	void updateSystemVariableIdNull() {
		final var systemVariable = new SystemVariable();
		systemVariable.setValue(RandomTestUtil.randomString());
		assertEquals(ID_REQUIRED_MESSAGE, assertThrows(IllegalArgumentException.class, () -> homematicCCU2Repository.updateSystemVariable(systemVariable)).getMessage());
	}
	
	@Test
	void updateSystemVariableValueNull() {
		final var systemVariable = new SystemVariable();
		systemVariable.setId("" + RandomTestUtil.randomInt());
		assertEquals(VALUE_REQUIRED_MESSAGE, assertThrows(IllegalArgumentException.class, () -> homematicCCU2Repository.updateSystemVariable(systemVariable)).getMessage());
	}

}
