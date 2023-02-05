package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		final Map<String, SystemVariable> systemVariables = List
				.of(newSystemVariable("DailyEvents", "T0:5.3;T1:7.52;T6:17.15"), newSystemVariable("LastBatchrun", "15.11.2022-22:36:04"), newSystemVariable("Month", "10"),
						newSystemVariable("Temperature", "3.210000"), newSystemVariable("Time", "0"), newSystemVariable("Workingday", "true"))
				.stream().collect(Collectors.toMap(SystemVariable::getName, Function.identity()));
		final Map<String, SystemVariable> existingSystemVariables = List
				.of(systemVariableWithId("DailyEvents", "T0:5:30;T1:7.53;T6:17.15"), systemVariableWithId("LastBatchrun", "14.11.2022-22:36:04"), systemVariableWithId("Month", "10"),
						systemVariableWithId("Temperature", "2.210000"), systemVariableWithId("Time", "0"), systemVariableWithId("Workingday", "true"))
				.stream().collect(Collectors.toMap(SystemVariable::getName, Function.identity()));
		Mockito.when(systemVariableRepository.readSystemVariables()).thenReturn(existingSystemVariables.values());
		final Collection<SystemVariable> updatedSystemVariables = new HashSet<>();
		Mockito.doAnswer(answer -> updatedSystemVariables.add(answer.getArgument(0, SystemVariable.class))).when(systemVariableRepository).updateSystemVariable(Mockito.any());

		systemVariableService.update(systemVariables.values());
		
		assertEquals(3, updatedSystemVariables.size());
		
		updatedSystemVariables.forEach(systemVariable -> {
			final var name = systemVariable.getName();
			assertEquals(systemVariables.get(name).getValue(), systemVariable.getValue());
			assertEquals(existingSystemVariables.get(name).getId(), systemVariable.getId());
		});
	}

	private SystemVariable newSystemVariable(final String name, final String value) {
		return new SystemVariable(name, value);
	}

	private SystemVariable systemVariableWithId(final String name, final String value) {
		final var systemVariable = newSystemVariable(name, value);
		systemVariable.setId("" + RandomTestUtil.randomInt());
		return systemVariable;

	}
	
	@Test
	void updateSystemVariableUnkown() {
		systemVariableService.update(List.of(newSystemVariable( RandomTestUtil.randomString(), RandomTestUtil.randomString())));
		
		Mockito.verify(systemVariableRepository, Mockito.never()).updateSystemVariable(Mockito.any());
	}
	@Test
	void read() {
		final var  systemVariables = List.of(mock(SystemVariable.class));
		when(systemVariableRepository.readSystemVariables()).thenReturn(systemVariables);
		
		assertEquals(systemVariables, systemVariableService.read());
	}

}
