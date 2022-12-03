package de.mq.iot2.rules.support;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.CalendarService.TimeType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.sysvars.SystemVariable;
@Rule(name = "Other-Variables-Rule", description = "\"Other-Variables-Rule", priority = 1)
public class OtherVariablesRulesImpl {
	static final String LAST_BATCH_RUN_DATE_FORMAT = "dd.MM.yyyy-HH:mm:ss";

	private static final int DEFAULT_PRIORITY = 2;

	private final static Logger LOGGER = LoggerFactory.getLogger(OtherVariablesRulesImpl.class);
	
	final static String WORKING_DAY_SYSTEM_VARIABLE_NAME="Workingday";
	final static String TIME_TYP_SYSTEM_VARIABLE_NAME="Time";
	final static String MONTH_SYSTEM_VARIABLE_NAME="Month";
	final static String TEMPERATURE_SYSTEM_VARIABLE_NAME="Temperature";
	final static String LAST_BATCH_RUN_VARIABLE_NAME="LastBatchrun";
	
	final static DecimalFormat DECIMAL_FORMAT_CCU2 = new DecimalFormat("#.000000", new DecimalFormatSymbols(Locale.US)); 
	@Condition
	public final boolean evaluate() {
		return true;
	}
	
	@Action(order = DEFAULT_PRIORITY)
	public final void workingday(@Fact("Cycle") final Cycle cycle,  @Fact("SystemVariables") final Collection<SystemVariable> systemVariables ) {
		Assert.notNull(cycle, "Cycle is required.");
		systemVariablesNotNullGuard(systemVariables);
		final var  systemVariable = new SystemVariable(WORKING_DAY_SYSTEM_VARIABLE_NAME, String.valueOf(cycle.isDeaultCycle()));
		systemVariables.add(systemVariable);
		writeSystemVariable2Logger(systemVariable);
	}

	private void writeSystemVariable2Logger(final SystemVariable systemVariable) {
		LOGGER.debug("Add SystemVariable {} value='{}'.", systemVariable.getName(), systemVariable.getValue());
	}

	private void systemVariablesNotNullGuard(final Collection<SystemVariable> systemVariables) {
		Assert.notNull(systemVariables, "SystemVariables shouldn't be null.");
	}
	
	
	@Action(order = DEFAULT_PRIORITY)
	public final void timeType(@Fact("TimeType") final TimeType timeType,  @Fact("SystemVariables") final Collection<SystemVariable> systemVariables ) {
		Assert.notNull(timeType, "TimeType is required.");
		systemVariablesNotNullGuard(systemVariables);
		final var  systemVariable = new SystemVariable(TIME_TYP_SYSTEM_VARIABLE_NAME, ""+ timeType.ordinal());
		systemVariables.add(systemVariable);
		writeSystemVariable2Logger(systemVariable);
	}
	
	@Action(order = DEFAULT_PRIORITY)
	public final void month(@Fact("Date") final LocalDate date,  @Fact("SystemVariables") final Collection<SystemVariable> systemVariables ) {
		Assert.notNull(date, "Date is required.");
		systemVariablesNotNullGuard(systemVariables);
	
		final var  systemVariable = new SystemVariable(MONTH_SYSTEM_VARIABLE_NAME, ""+ date.getMonth().ordinal());
		
		systemVariables.add(systemVariable);
		writeSystemVariable2Logger(systemVariable);
	}
	
	@Action(order = DEFAULT_PRIORITY)
	public final void maxTemperature(@Fact("MaxForecastTemperature") final Optional<Double> maxForecastTemperature,  @Fact("SystemVariables") final Collection<SystemVariable> systemVariables ) {
		maxForecastTemperature.ifPresent(temperature -> addTemperatureSystemVariable(systemVariables, temperature));
	}

	private void addTemperatureSystemVariable(final Collection<SystemVariable> systemVariables, Double temperature) {
		final var systemVariable = new SystemVariable(TEMPERATURE_SYSTEM_VARIABLE_NAME , DECIMAL_FORMAT_CCU2.format(temperature));
		systemVariables.add(systemVariable);
		writeSystemVariable2Logger(systemVariable);
	}
	
	
	
	@Action(order = DEFAULT_PRIORITY)
	public final void lastBatchrun(@Fact("SystemVariables") final Collection<SystemVariable> systemVariables ) {
		systemVariablesNotNullGuard(systemVariables);
		final var  systemVariable = new SystemVariable(LAST_BATCH_RUN_VARIABLE_NAME, LocalDateTime.now().format(DateTimeFormatter.ofPattern(LAST_BATCH_RUN_DATE_FORMAT)));
		systemVariables.add(systemVariable);
		writeSystemVariable2Logger(systemVariable);
	}
}
