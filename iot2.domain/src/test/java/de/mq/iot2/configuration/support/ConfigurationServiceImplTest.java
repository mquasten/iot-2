package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;

import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleRepository;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;

class ConfigurationServiceImplTest {

	private final ConversionService conversionService = Mockito.mock(ConversionService.class);
	private final Cycle workingDayCycle = Mockito.mock(Cycle.class);
	private final Cycle nonWorkingDayCycle = Mockito.mock(Cycle.class);
	private final Cycle otherTimesCycle = Mockito.mock(Cycle.class);
	private final CycleRepository cycleRepository = Mockito.mock(CycleRepository.class);
	private final ConfigurationRepository configurationRepository = Mockito.mock(ConfigurationRepository.class);
	private final ParameterRepository parameterRepository = Mockito.mock(ParameterRepository.class);
	private final ConfigurationService configurationService = new ConfigurationServiceImpl(configurationRepository, parameterRepository, cycleRepository, conversionService);

	@Test
	void createDefaultConfigurationsAndParameters() {

		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.NON_WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(nonWorkingDayCycle));
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(workingDayCycle));
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.OTHER_TIMES_CYCLE_ID))).thenReturn(Optional.of(otherTimesCycle));

		final Map<RuleKey, Configuration> savedConfigurations = new HashMap<>();
		Mockito.doAnswer(answer -> {
			final var configuration = answer.getArgument(0, Configuration.class);
			savedConfigurations.put(configuration.key(), configuration);
			return configuration;
		}).when(configurationRepository).save(Mockito.any(Configuration.class));

		final Collection<Parameter> savedParameter = new ArrayList<>();

		Mockito.doAnswer(answer -> {
			final var parameter = answer.getArgument(0, Parameter.class);
			savedParameter.add(parameter);
			return parameter;
		}).when(parameterRepository).save(Mockito.any(Parameter.class));

		configurationService.createDefaultConfigurationsAndParameters();

		assertTrue(savedConfigurations.containsKey(RuleKey.CleanUp));
		assertTrue(savedConfigurations.containsKey(RuleKey.EndOfDay));
		assertEquals(2, savedConfigurations.size());

		assertEquals(12, savedParameter.size()); 

		final List<? extends Parameter> cleanUpParameter = savedParameter.stream().filter(parameter -> parameter.configuration() == savedConfigurations.get(RuleKey.CleanUp))
				.collect(Collectors.toList());
		assertEquals(1, cleanUpParameter.size());
		assertEquals(Key.DaysBack, cleanUpParameter.get(0).key());
		assertTrue(cleanUpParameter.get(0) instanceof ParameterImpl);

		final Collection<Key> globalEndOfDayParameter = savedParameter.stream()
				.filter(parameter -> parameter.configuration() == savedConfigurations.get(RuleKey.EndOfDay) && parameter instanceof ParameterImpl).map(Parameter::key).collect(Collectors.toList());
		assertEquals(8, globalEndOfDayParameter.size());
		assertTrue(globalEndOfDayParameter.containsAll(Arrays.asList(Key.UpTime, Key.MinSunDownTime,Key.MaxSunDownTime, Key.MinSunUpTime, Key.MaxSunUpTime, Key.SunUpDownType, Key.ShadowTemperature, Key.ShadowTime)));

		final Collection<? extends Parameter> endOfDayCycleParameters = savedParameter.stream()
				.filter(parameter -> (parameter.configuration() == savedConfigurations.get(RuleKey.EndOfDay)) && (parameter instanceof CycleParameterImpl)).collect(Collectors.toList());
		assertEquals(3, endOfDayCycleParameters.size());
		endOfDayCycleParameters.forEach(parameter -> {
			assertEquals(Key.UpTime, parameter.key());
			assertTrue(List.of(workingDayCycle, nonWorkingDayCycle, otherTimesCycle).contains(((CycleParameter) parameter).cycle()));

		});

		Mockito.verify(parameterRepository).deleteByConfiguration(savedConfigurations.get(RuleKey.EndOfDay));
		Mockito.verify(parameterRepository).deleteByConfiguration(savedConfigurations.get(RuleKey.CleanUp));
	}

	@Test
	void createDefaultConfigurationsAndParametersWorkingDayCycleNotFoud() {
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.NON_WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(nonWorkingDayCycle));
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.OTHER_TIMES_CYCLE_ID))).thenReturn(Optional.of(otherTimesCycle));
		assertEquals(ConfigurationServiceImpl.WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE,
				assertThrows(EntityNotFoundException.class, () -> configurationService.createDefaultConfigurationsAndParameters()).getMessage());
	}

	@Test
	void createDefaultConfigurationsAndParametersNonWorkingDayCycleNotFoud() {
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(workingDayCycle));
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.OTHER_TIMES_CYCLE_ID))).thenReturn(Optional.of(otherTimesCycle));
		assertEquals(ConfigurationServiceImpl.NON_WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE,
				assertThrows(EntityNotFoundException.class, () -> configurationService.createDefaultConfigurationsAndParameters()).getMessage());
	}
	
	@Test
	void createDefaultConfigurationsAndParametersOtherTimesCycleNotFoud() {
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(workingDayCycle));
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.NON_WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(nonWorkingDayCycle));
		assertEquals(ConfigurationServiceImpl.OTHER_TIMES_CYCLE_NOT_FOUND_MESSAGE,
				assertThrows(EntityNotFoundException.class, () -> configurationService.createDefaultConfigurationsAndParameters()).getMessage());
	}

	@Test
	void parameters() {
		final var configuration = Mockito.mock(Configuration.class);
		Mockito.when(configurationRepository.findByKey(RuleKey.EndOfDay)).thenReturn(Optional.of(configuration));
		final var minSunDown = new ParameterImpl(configuration, Key.MinSunDownTime, "17:15");
		final var maxSunDown = new ParameterImpl(configuration, Key.MaxSunDownTime, "22:15");
		final var minSunUp = new ParameterImpl(configuration, Key.MinSunUpTime, "05:30");
		final var maxSunUp = new ParameterImpl(configuration, Key.MaxSunUpTime, "10:00");
		final var parameterUpTime = new ParameterImpl(configuration, Key.UpTime, "05:30");
		final var parameterSunUpDownType = new ParameterImpl(configuration, Key.SunUpDownType, TwilightType.Mathematical.name());
		final var parameterShadowTemperature = new ParameterImpl(configuration, Key.ShadowTemperature, "25.25");
		final var parameterShadowTime = new ParameterImpl(configuration, Key.ShadowTime, "09.00");
		
		final var nonWorkingDayCycleParameterUpTime = new CycleParameterImpl(configuration, Key.UpTime, "07:30", nonWorkingDayCycle);

		Mockito.doAnswer(answer -> convertParameterValue( answer.getArgument(0, String.class))).when(conversionService).convert(Mockito.any(), Mockito.any());
		Mockito.when(parameterRepository.findByConfiguration(configuration)).thenReturn(List.of(minSunDown, maxSunDown, minSunUp, maxSunUp, parameterUpTime, parameterSunUpDownType, nonWorkingDayCycleParameterUpTime, parameterShadowTemperature, parameterShadowTime));

		Map<Key, ? extends Object> results = configurationService.parameters(RuleKey.EndOfDay, nonWorkingDayCycle);

		assertEquals(8, results.size());
		assertTrue(results.keySet().containsAll(List.of(Key.MinSunDownTime, Key.MaxSunDownTime, Key.MaxSunUpTime, Key.UpTime, Key.MinSunDownTime,  Key.MinSunDownTime, Key.ShadowTemperature, Key.ShadowTime)));
		assertEquals(LocalTime.parse(nonWorkingDayCycleParameterUpTime.value()), results.get(Key.UpTime));
	}

	private Object convertParameterValue(final String value) {
		if( value.contains(":")) {
			return  LocalTime.parse(value);
		} 
		if( value.contains(".")) {
			return Double.parseDouble(value);
		}
		return TwilightType.valueOf(value);
	}

	@Test
	void parametersConfigurationNotFound() {
		assertEquals(String.format(ConfigurationServiceImpl.CONFIGURATION_KEY_NOT_FOUND_MESSAGE_PATTERN, RuleKey.EndOfDay),
				assertThrows(EntityNotFoundException.class, () -> configurationService.parameters(RuleKey.EndOfDay, nonWorkingDayCycle)).getMessage());
	}

	@Test
	void parametersMissingRequiredArguments() {
		assertThrows(IllegalArgumentException.class, () -> configurationService.parameters(null, nonWorkingDayCycle));
		assertThrows(IllegalArgumentException.class, () -> configurationService.parameters(RuleKey.EndOfDay, null));
	}

}
