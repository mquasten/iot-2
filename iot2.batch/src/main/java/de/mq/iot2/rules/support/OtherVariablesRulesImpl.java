package de.mq.iot2.rules.support;

import java.util.Collection;


import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.sysvars.SystemVariable;
@Rule(name = "Other-Variables-Rule", description = "\"Other-Variables-Rule", priority = 1)
public class OtherVariablesRulesImpl {
	private final static Logger LOGGER = LoggerFactory.getLogger(OtherVariablesRulesImpl.class);
	
	final static String WORKING_DAY_SYSTEM_VARIABLE_NAME="Workingday";
	@Condition
	public final boolean evaluate() {
		return true;
	}
	
	@Action(order = 2)
	public final void workingday(@Fact("Cycle") final Cycle cycle,  @Fact("SystemVariables") final Collection<SystemVariable> systemVariables ) {
		final var  systemVariable = new SystemVariable(WORKING_DAY_SYSTEM_VARIABLE_NAME, String.valueOf(cycle.isDeaultCycle()));
		systemVariables.add(systemVariable);
		LOGGER.debug("Add SystemVariable {} value='{}'.", systemVariable.getName(), systemVariable.getValue());
	}
}
