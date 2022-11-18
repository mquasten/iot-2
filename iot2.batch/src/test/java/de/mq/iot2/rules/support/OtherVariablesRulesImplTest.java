package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.sysvars.SystemVariable;

class OtherVariablesRulesImplTest {
	private final OtherVariablesRulesImpl otherVariablesRules = new OtherVariablesRulesImpl();	
	
	@Test
	void evaluate() {
		assertTrue(otherVariablesRules.evaluate());
	}
	
	@Test
	void workingdayTrue() {
		final Cycle cycle = Mockito.mock(Cycle.class);
		Mockito.when(cycle.isDeaultCycle()).thenReturn(true);
		
		final Collection<SystemVariable> systemVariables = new ArrayList<>();
		otherVariablesRules.workingday(cycle, systemVariables);
		
		assertEquals(1, systemVariables.size());
		assertEquals(OtherVariablesRulesImpl.WORKING_DAY_SYSTEM_VARIABLE_NAME, systemVariables.stream().findAny().get().getName());
		assertEquals(""+ true, systemVariables.stream().findAny().get().getValue());
	}
	
	@Test
	void workingdayFalse() {
		final Collection<SystemVariable> systemVariables = new ArrayList<>();
		otherVariablesRules.workingday(Mockito.mock(Cycle.class), systemVariables);
		
		assertEquals(1, systemVariables.size());
		
		assertEquals(""+ false, systemVariables.stream().findAny().get().getValue());
	}
	
}
