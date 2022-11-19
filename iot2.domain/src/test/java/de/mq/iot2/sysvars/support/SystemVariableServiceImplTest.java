package de.mq.iot2.sysvars.support;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.support.RandomTestUtil;
import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;

class SystemVariableServiceImplTest {
	
	private final SystemVariableRepository systemVariableRepository = Mockito.mock(SystemVariableRepository.class);
	
	private final SystemVariableService systemVariableService = new SystemVariableServiceImpl(systemVariableRepository);
	
	@Test
	void update() {
		final var systemVariables = List.of(newSystemVariable("DailyEvents","T0:5.3;T1:7.52;T6:17.15"), newSystemVariable("LastBatchrun", "15.11.2022-22:36:04"),
				newSystemVariable("Month", "10"), newSystemVariable("Temperature" , "22.210000"), newSystemVariable("Time", "0"), newSystemVariable("Workingday", "true"));
		Mockito.when(systemVariableRepository.readSystemVariables()).thenReturn(systemVariables);
	
	    systemVariableService.update(systemVariables);
	}
	
	private SystemVariable newSystemVariable(final String name, final String value) {
		final var systemVariable = new SystemVariable(name, value);
		systemVariable.setId(""+RandomTestUtil.randomInt());
		return systemVariable;
	}

}
