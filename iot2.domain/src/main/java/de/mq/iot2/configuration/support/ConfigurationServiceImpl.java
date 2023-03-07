package de.mq.iot2.configuration.support;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
import jakarta.persistence.EntityNotFoundException;

@Service
class ConfigurationServiceImpl implements ConfigurationService {

	static final String NON_WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE = "Non Workingday Cycle not found.";
	static final String CONFIGURATION_KEY_NOT_FOUND_MESSAGE_PATTERN = "Configuration with key %s not found.";
	static final String WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE = "Workingday Cycle not found.";
	static final String OTHER_TIMES_CYCLE_NOT_FOUND_MESSAGE = "Other Times Cycle not found.";

	static final String PARAMETER_ID_NOT_FOUND_MESSAGE_PATTERN = "Parameter with id %s not found.";

	private static final long CLEAN_UP_CONFIGURATION_ID = 2L;
	static final long OTHER_TIMES_CYCLE_ID = 3L;
	static final long WORKING_DAY_CYCLE_ID = 2L;
	static final long NON_WORKING_DAY_CYCLE_ID = 1L;
	private final ConfigurationRepository configurationRepository;
	private final ParameterRepository parameterRepository;
	private final CycleRepository cycleRepository;
	private final ConversionService conversionService;

	ConfigurationServiceImpl(ConfigurationRepository configurationRepository, final ParameterRepository parameterRepository, final CycleRepository cycleRepository,
			final ConversionService conversionService) {
		this.configurationRepository = configurationRepository;
		this.parameterRepository = parameterRepository;
		this.cycleRepository = cycleRepository;
		this.conversionService = conversionService;
	}

	@Override
	@Transactional
	public void createDefaultConfigurationsAndParameters() {

		final var nonWorkingDayCycle = cycleRepository.findById(IdUtil.id(NON_WORKING_DAY_CYCLE_ID))
				.orElseThrow(() -> new EntityNotFoundException(NON_WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE));
		final var workingDayCycle = cycleRepository.findById(IdUtil.id(WORKING_DAY_CYCLE_ID)).orElseThrow(() -> new EntityNotFoundException(WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE));
		final var otherTimesCycle = cycleRepository.findById(IdUtil.id(OTHER_TIMES_CYCLE_ID)).orElseThrow(() -> new EntityNotFoundException(OTHER_TIMES_CYCLE_NOT_FOUND_MESSAGE));
		saveEndOfDayConfiguration(nonWorkingDayCycle, workingDayCycle, otherTimesCycle);
		saveCleanUpConfiguration();
	}

	private void saveCleanUpConfiguration() {
		final var cleanUpConfiguration = configurationRepository.save(new ConfigurationImpl(CLEAN_UP_CONFIGURATION_ID, RuleKey.CleanUp, "CleanUpBatch"));
		parameterRepository.deleteByConfiguration(cleanUpConfiguration);
		parameterRepository.save(new ParameterImpl(cleanUpConfiguration, Key.DaysBack, "30"));
	}

	private void saveEndOfDayConfiguration(final Cycle nonWorkingDayCycle, final Cycle workingDayCycle, final Cycle otherTimesCycle) {
		final var endOfBayConfiguration = configurationRepository.save(new ConfigurationImpl(1L, RuleKey.EndOfDay, "EndofDayBatch"));
		parameterRepository.deleteByConfiguration(endOfBayConfiguration);
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MinSunUpTime, "05:30"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MaxSunUpTime, "10:00"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MinSunDownTime, "17:15"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MaxSunDownTime, "22:15"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.UpTime, "07:15"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.SunUpDownType, TwilightType.Mathematical.name()));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.ShadowTemperature, "25"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.ShadowTime, "09:00"));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "07:15", nonWorkingDayCycle));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "06:30", otherTimesCycle));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "05:30", workingDayCycle));
	}

	@Transactional
	@Override
	public Map<Key, Object> parameters(final RuleKey ruleKey, final Cycle cycle) {
		Assert.notNull(ruleKey, "Key is required");
		Assert.notNull(cycle, "Cycle is required");
		final var configuration = configurationRepository.findByKey(ruleKey)
				.orElseThrow(() -> new EntityNotFoundException(String.format(CONFIGURATION_KEY_NOT_FOUND_MESSAGE_PATTERN, ruleKey)));

		final Collection<? extends Parameter> parameters = parameterRepository.findByConfiguration(configuration);

		final Map<Key, Parameter> cycleParameters = parameters.stream().filter(parameter -> parameter instanceof CycleParameter)
				.filter(parameter -> ((CycleParameter) parameter).cycle().equals(cycle)).collect(Collectors.toMap(Parameter::key, Function.identity()));

		final Collection<Parameter> globalParameters = parameters.stream().filter(parameter -> !cycleParameters.containsKey(parameter.key())).collect(Collectors.toList());

		final Map<Key, Object> results = Stream.concat(cycleParameters.values().stream(), globalParameters.stream())
				.map(parameter -> new SimpleImmutableEntry<>(parameter.key(), conversionService.convert(parameter.value(), parameter.key().type())))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return results;

	}

	@Transactional
	@Override
	public <T> Optional<T> parameter(final RuleKey ruleKey, final Key key, final Class<T> clazz) {
		return parameterRepository.findByRuleKeyAndKey(ruleKey, key).map(parameter -> conversionService.convert(parameter.value(), clazz));
	}

	@Transactional
	@Override
	public Collection<Configuration> configurations() {
		return configurationRepository.findAll();
	}

	@Transactional
	@Override
	public Collection<Parameter> parameters(final String configurationId) {
		Assert.hasText(configurationId, "ConfigurationId is required.");
		return parameterRepository.findByConfigurationId(configurationId);
	}

	@Transactional()
	@Override
	public void save(final Parameter parmeter) {
		Assert.notNull(parmeter, "Parameter is required.");
		parameterRepository.save(parmeter);
	}
	
	@Override
	@Transactional
	public void export(final OutputStream os) {
		try (final PrintWriter writer = new PrintWriter(os)) {
			
		}
	}
}
