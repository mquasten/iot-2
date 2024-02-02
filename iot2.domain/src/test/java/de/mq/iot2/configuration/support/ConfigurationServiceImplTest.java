package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.calendar.support.CycleImpl;
import de.mq.iot2.calendar.support.CycleRepository;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;
import jakarta.persistence.EntityNotFoundException;

class ConfigurationServiceImplTest {

	private static final String CSV_DELIMITER = ";";
	private final ConversionService conversionService = Mockito.mock(ConversionService.class);
	private final Cycle workingDayCycle = Mockito.mock(Cycle.class);
	private final Cycle nonWorkingDayCycle = Mockito.mock(Cycle.class);
	private final Cycle otherTimesCycle = Mockito.mock(Cycle.class);
	private final CycleRepository cycleRepository = Mockito.mock(CycleRepository.class);
	private final ConfigurationRepository configurationRepository = Mockito.mock(ConfigurationRepository.class);
	private final ParameterRepository parameterRepository = Mockito.mock(ParameterRepository.class);
	@SuppressWarnings("unchecked")
	private final Converter<Pair<Parameter, Boolean>, String[]> parameterCsvConverter = Mockito.mock(Converter.class);
	@SuppressWarnings("unchecked")
	private final Converter<Pair<String[], Pair<Map<String, Configuration>, Map<String, Cycle>>>, Parameter> arrayCsvConverter = Mockito.mock(Converter.class);
	private final ConfigurationService configurationService = new ConfigurationServiceImpl(configurationRepository, parameterRepository, cycleRepository, conversionService,
			parameterCsvConverter, arrayCsvConverter, CSV_DELIMITER);

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
				.filter(parameter -> parameter.configuration() == savedConfigurations.get(RuleKey.EndOfDay) && parameter instanceof ParameterImpl).map(Parameter::key)
				.collect(Collectors.toList());
		assertEquals(8, globalEndOfDayParameter.size());
		assertTrue(globalEndOfDayParameter.containsAll(
				Arrays.asList(Key.UpTime, Key.MinSunDownTime, Key.MaxSunDownTime, Key.MinSunUpTime, Key.MaxSunUpTime, Key.SunUpDownType, Key.ShadowTemperature, Key.ShadowTime)));

		final Collection<? extends Parameter> endOfDayCycleParameters = savedParameter.stream()
				.filter(parameter -> (parameter.configuration() == savedConfigurations.get(RuleKey.EndOfDay)) && (parameter instanceof CycleParameterImpl))
				.collect(Collectors.toList());
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

	@SuppressWarnings("unchecked")
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

		doAnswer(answer -> convertParameterValue(answer.getArgument(0, String.class))).when(conversionService).convert(Mockito.any(Object.class), Mockito.any(Class.class));
		when(parameterRepository.findByConfiguration(configuration)).thenReturn(List.of(minSunDown, maxSunDown, minSunUp, maxSunUp, parameterUpTime, parameterSunUpDownType,
				nonWorkingDayCycleParameterUpTime, parameterShadowTemperature, parameterShadowTime));

		Map<Key, ? extends Object> results = configurationService.parameters(RuleKey.EndOfDay, nonWorkingDayCycle);

		assertEquals(8, results.size());
		assertTrue(results.keySet().containsAll(
				List.of(Key.MinSunDownTime, Key.MaxSunDownTime, Key.MaxSunUpTime, Key.UpTime, Key.MinSunDownTime, Key.MinSunDownTime, Key.ShadowTemperature, Key.ShadowTime)));
		assertEquals(LocalTime.parse(nonWorkingDayCycleParameterUpTime.value()), results.get(Key.UpTime));
	}

	private Object convertParameterValue(final String value) {
		if (value.contains(":")) {
			return LocalTime.parse(value);
		}
		if (value.contains(".")) {
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

	@Test
	void parameter() {
		final var value = 30;
		final var parameter = Mockito.mock(Parameter.class);
		Mockito.when(parameter.value()).thenReturn("" + value);
		Mockito.when(conversionService.convert("" + value, Integer.class)).thenReturn(value);
		Mockito.when(parameterRepository.findByRuleKeyAndKey(RuleKey.CleanUp, Key.DaysBack)).thenReturn(Optional.of(parameter));
		assertEquals(Optional.of(value), configurationService.parameter(RuleKey.CleanUp, Key.DaysBack, Integer.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void parameterNotExists() {
		Mockito.when(conversionService.convert(Mockito.any(Object.class), Mockito.any(Class.class))).thenReturn(30);
		assertEquals(Optional.empty(), configurationService.parameter(RuleKey.CleanUp, Key.DaysBack, Integer.class));
	}

	@Test
	void configurations() {
		final Collection<Configuration> configurations = List.of(Mockito.mock(Configuration.class), Mockito.mock(Configuration.class));
		Mockito.when(configurationRepository.findAll()).thenReturn(configurations);
		assertEquals(configurations, configurationService.configurations());
	}

	@Test
	void parametersByConfigurationId() {
		final var configurationId = RandomTestUtil.randomString();
		final Collection<Parameter> parameters = List.of(Mockito.mock(Parameter.class), Mockito.mock(Parameter.class));
		Mockito.when(parameterRepository.findByConfigurationId(configurationId)).thenReturn(parameters);
		assertEquals(parameters, configurationService.parameters(configurationId));
	}

	@ParameterizedTest
	@EmptySource()
	@NullSource()
	@ValueSource(strings = " ")
	void parametersByConfigurationIdWithEmpty(final String id) {
		assertThrows(IllegalArgumentException.class, () -> configurationService.parameters(id));
	}

	@Test
	void save() {
		final Parameter parameter = new ParameterImpl(Mockito.mock(Configuration.class), Key.MaxSunDownTime, RandomTestUtil.randomString());
		configurationService.save(parameter);
		Mockito.verify(parameterRepository).save(parameter);
	}

	@ParameterizedTest
	@NullSource
	void saveNull(final Parameter parameter) {
		assertThrows(IllegalArgumentException.class, () -> configurationService.save(parameter));
	}

	@Test
	void export() throws IOException {
		final Configuration configurationEndOfDay = new ConfigurationImpl(1L, RuleKey.EndOfDay, "EndofDayBatch");
		final Configuration configurationCeanup = new ConfigurationImpl(2L, RuleKey.CleanUp, "Cleanup");
		final Cycle cycleFreizeit = BeanUtils.instantiateClass(CycleImpl.class);
		final Cycle cycleArbeitsZeit = BeanUtils.instantiateClass(CycleImpl.class);
		final Cycle cycleAbweichendeZeiten = BeanUtils.instantiateClass(CycleImpl.class);
		final Parameter globalParameter = new ParameterImpl(configurationEndOfDay, Key.UpTime, "07:15");
		final Parameter freizeitParameter = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "07:00", cycleFreizeit);
		final Parameter arbeitszeitParameter = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "05:15", cycleArbeitsZeit);
		final Parameter abweichendeZeitenParameter = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "06:00", cycleAbweichendeZeiten);
		final Parameter otherGlobalParameter = new ParameterImpl(configurationEndOfDay, Key.ShadowTemperature, "25");
		final Parameter daysBackParameter = new ParameterImpl(configurationCeanup, Key.DaysBack, "30");

		Mockito.when(parameterRepository.findAll())
				.thenReturn(List.of(daysBackParameter, abweichendeZeitenParameter, arbeitszeitParameter, otherGlobalParameter, freizeitParameter, globalParameter));
		Mockito.doAnswer(a -> {
			@SuppressWarnings("unchecked")
			final Pair<Parameter, Boolean> pair = a.getArgument(0, Pair.class);
			return new String[] { IdUtil.getId(pair.getFirst()), !pair.getSecond() ? pair.getFirst().configuration().name() : "", "..." };
		}).when(parameterCsvConverter).convert(Mockito.any());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {

			configurationService.export(os);

			final Map<String, String> results = CollectionUtils.arrayToList(os.toString().split("\n")).stream().map(Object::toString)
					.collect(Collectors.toMap(x -> x.split(String.format("[%s]", CSV_DELIMITER))[0], x -> x.split(String.format("[%s]", CSV_DELIMITER))[1]));

			assertEquals(6, results.size());
			assertEquals(configurationCeanup.name(), results.get(IdUtil.getId(daysBackParameter)));
			final List<Parameter> endOfDayParameters = List.of(abweichendeZeitenParameter, arbeitszeitParameter, otherGlobalParameter, freizeitParameter, globalParameter).stream()
					.sorted((p1, p2) -> p1.key().name().compareTo(p2.key().name())).collect(Collectors.toList());
			assertEquals(configurationEndOfDay.name(), results.get(IdUtil.getId(endOfDayParameters.get(0))));
			IntStream.range(1, endOfDayParameters.size()).forEach(i -> assertTrue(results.get(IdUtil.getId(endOfDayParameters.get(i))).isEmpty()));

		}

	}

	@Test
	void importCsv() throws IOException {
		final Configuration configurationCleanUp = new ConfigurationImpl(2, RuleKey.CleanUp, "CleanUpBatch");
		final Configuration configurationEndOfDay = new ConfigurationImpl(1, RuleKey.EndOfDay, "EndofDayBatch");
		final Cycle cycleFreizeit = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycleFreizeit, IdUtil.id(1L));
		final Cycle cycleArbeitszeit = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycleArbeitszeit, IdUtil.id(2L));
		final Cycle cycleAbweichenderTagesbeginn = BeanUtils.instantiateClass(CycleImpl.class);
		IdUtil.assignId(cycleAbweichenderTagesbeginn, IdUtil.id(3L));

		final Parameter cleanUpParameter = new ParameterImpl(configurationCleanUp, Key.DaysBack, "30");
		final Parameter maxSunDownTimeParameter = new ParameterImpl(configurationEndOfDay, Key.MaxSunDownTime, "22:15");
		final Parameter upTimeParameterFreizeit = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "07:00", cycleFreizeit);
		final Parameter upTimeParameterArbeitszeit = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "05:15", cycleArbeitszeit);
		final Parameter upTimeParameterAbweichenderTagesbeginn = new CycleParameterImpl(configurationEndOfDay, Key.UpTime, "06:30", cycleAbweichenderTagesbeginn);
		final Parameter upTimeParameter = new ParameterImpl(configurationEndOfDay, Key.UpTime, "07:15");
		final Map<String, Parameter> parameter = Map.of(cleanUpParameter.value(), cleanUpParameter, maxSunDownTimeParameter.value(), maxSunDownTimeParameter,
				upTimeParameterFreizeit.value(), upTimeParameterFreizeit, upTimeParameterArbeitszeit.value(), upTimeParameterArbeitszeit,
				upTimeParameterAbweichenderTagesbeginn.value(), upTimeParameterAbweichenderTagesbeginn, upTimeParameter.value(), upTimeParameter);

		Mockito.when(cycleRepository.findAll()).thenReturn(List.of(cycleFreizeit, cycleArbeitszeit, cycleAbweichenderTagesbeginn));
		Mockito.doAnswer(answer -> {
			@SuppressWarnings("unchecked")
			final Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>> pair = answer.getArgument(0, Pair.class);
			assertEquals(7, pair.getFirst().length);
			assertEquals(3, pair.getSecond().getSecond().size());
			return parameter.get(pair.getFirst()[2]);
		}).when(arrayCsvConverter).convert(Mockito.any());

		try (final InputStream is = getClass().getClassLoader().getResourceAsStream("configuration.csv")) {
			configurationService.importCsv(is);
		}

		Mockito.verify(parameterRepository, Mockito.times(1)).save(cleanUpParameter);
		Mockito.verify(parameterRepository, Mockito.times(1)).save(maxSunDownTimeParameter);
		Mockito.verify(parameterRepository, Mockito.times(1)).save(upTimeParameterFreizeit);
		Mockito.verify(parameterRepository, Mockito.times(1)).save(upTimeParameterArbeitszeit);
		Mockito.verify(parameterRepository, Mockito.times(1)).save(upTimeParameterAbweichenderTagesbeginn);
		Mockito.verify(parameterRepository, Mockito.times(1)).save(upTimeParameter);
		Mockito.verifyNoMoreInteractions(parameterRepository);
		Mockito.verify(configurationRepository, Mockito.times(1)).save(configurationCleanUp);
		Mockito.verify(configurationRepository, Mockito.times(1)).save(configurationEndOfDay);
		Mockito.verifyNoMoreInteractions(configurationRepository);
		Mockito.verify(cycleRepository).findAll();
	}

	@Test
	void importCsvWrongNUmberOfColumns() throws IOException {
		try (final InputStream is = new ByteArrayInputStream(";".getBytes());) {
			assertEquals(String.format(ConfigurationServiceImpl.WRONG_NUMBER_OF_COLUMNS_MESSAGE, 1),
					assertThrows(IllegalArgumentException.class, () -> configurationService.importCsv(is)).getMessage());
		}
	}
	
	@Test
	void removeConfigurations() {
		boolean[] parameterDeleted= {false};
		Mockito.doAnswer(answer -> {
			parameterDeleted[0]=true;
			return null;
		}).when(parameterRepository).deleteAll();
		Mockito.doAnswer(answer -> {
			assertTrue(parameterDeleted[0]);
			return null;
		}).when(configurationRepository).deleteAll();
		
		configurationService.removeConfigurations();
		
		assertTrue(parameterDeleted[0]);
		Mockito.verify(parameterRepository, Mockito.times(1)).deleteAll();
		Mockito.verify(configurationRepository, Mockito.times(1)).deleteAll();
	}

}
