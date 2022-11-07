package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	private final Cycle workingDayCycle = Mockito.mock(Cycle.class);
	private final Cycle nonWorkingDayCycle = Mockito.mock(Cycle.class);
	private final CycleRepository cycleRepository = Mockito.mock(CycleRepository.class);
	private final ConfigurationRepository configurationRepository = Mockito.mock(ConfigurationRepository.class);
	private final ParameterRepository parameterRepository = Mockito.mock(ParameterRepository.class);

	private final ConfigurationService configurationService = new ConfigurationServiceImpl(configurationRepository, parameterRepository, cycleRepository);

	@Test
	void createDefaultConfigurationsAndParameters() {

		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.NON_WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(nonWorkingDayCycle));
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(workingDayCycle));

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

		assertEquals(6, savedParameter.size());

		final List<? extends Parameter> cleanUpParameter = savedParameter.stream().filter(parameter -> parameter.configuration() == savedConfigurations.get(RuleKey.CleanUp))
				.collect(Collectors.toList());
		assertEquals(1, cleanUpParameter.size());
		assertEquals(Key.DaysBack, cleanUpParameter.get(0).key());
		assertTrue(cleanUpParameter.get(0) instanceof ParameterImpl);

		final Collection<Key> globalEndOfDayParameter = savedParameter.stream()
				.filter(parameter -> parameter.configuration() == savedConfigurations.get(RuleKey.EndOfDay) && parameter instanceof ParameterImpl).map(Parameter::key).collect(Collectors.toList());
		assertEquals(3, globalEndOfDayParameter.size());
		assertTrue(globalEndOfDayParameter.containsAll(Arrays.asList(Key.UpTime, Key.MinSunDownTime, Key.MaxSunUpTime)));

		final Collection<? extends Parameter> endOfDayCycleParameters = savedParameter.stream()
				.filter(parameter -> (parameter.configuration() == savedConfigurations.get(RuleKey.EndOfDay)) && (parameter instanceof CycleParameterImpl)).collect(Collectors.toList());
		assertEquals(2, endOfDayCycleParameters.size());
		endOfDayCycleParameters.forEach(parameter -> {
			assertEquals(Key.UpTime, parameter.key());
			assertTrue(List.of(workingDayCycle, nonWorkingDayCycle).contains(((CycleParameter) parameter).cycle()));

		});

		Mockito.verify(parameterRepository).deleteByConfiguration(savedConfigurations.get(RuleKey.EndOfDay));
		Mockito.verify(parameterRepository).deleteByConfiguration(savedConfigurations.get(RuleKey.CleanUp));
	}

	@Test
	void createDefaultConfigurationsAndParametersWorkingDayCycleNotFoud() {
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.NON_WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(nonWorkingDayCycle));
		assertEquals(ConfigurationServiceImpl.WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE,
				assertThrows(EntityNotFoundException.class, () -> configurationService.createDefaultConfigurationsAndParameters()).getMessage());
	}

	@Test
	void createDefaultConfigurationsAndParametersNonWorkingDayCycleNotFoud() {
		Mockito.when(cycleRepository.findById(IdUtil.id(ConfigurationServiceImpl.WORKING_DAY_CYCLE_ID))).thenReturn(Optional.of(workingDayCycle));
		assertEquals(ConfigurationServiceImpl.NON_WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE,
				assertThrows(EntityNotFoundException.class, () -> configurationService.createDefaultConfigurationsAndParameters()).getMessage());
	}

}
