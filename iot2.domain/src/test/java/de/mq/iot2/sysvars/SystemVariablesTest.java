package de.mq.iot2.sysvars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class SystemVariablesTest {

	@Test
	void systemVariables() {
		List<SystemVariable> systemVariableList = List.of(new SystemVariable(), new SystemVariable());
		SystemVariables systemVariables = new SystemVariables();
		assertTrue(systemVariables.getSystemVariables().size() == 0);

		systemVariables.setSystemVariables(systemVariableList);

		assertEquals(systemVariableList, systemVariables.getSystemVariables());
	}

}
