package de.mq.iot2.configuration.support;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleRepository;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;

@Service
class ConfigurationServiceImpl implements ConfigurationService {

	static final String NON_WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE = "Non Workingday Cycle not found.";
	static final String CYCLE_KEY_NOT_FOUND_MESSAGE_PATTERN = "Cycle with key %s not found.";
	static final String WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE = "Workingday Cycle not found.";
	private static final long CLEAN_UP_CONFIGURATION_ID = 2L;
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

		final var nonWorkingDayCycle = cycleRepository.findById(IdUtil.id(NON_WORKING_DAY_CYCLE_ID)).orElseThrow(() -> new EntityNotFoundException(NON_WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE));
		final var workingDayCycle = cycleRepository.findById(IdUtil.id(WORKING_DAY_CYCLE_ID)).orElseThrow(() -> new EntityNotFoundException(WORKINGDAY_CYCLE_NOT_FOUND_MESSAGE));
		saveEndOfDayConfiguration(nonWorkingDayCycle, workingDayCycle);
		saveCleanUpConfiguration();
	}

	private void saveCleanUpConfiguration() {
		final var cleanUpConfiguration = configurationRepository.save(new ConfigurationImpl(CLEAN_UP_CONFIGURATION_ID, RuleKey.CleanUp, "CleanUpBatch"));
		parameterRepository.deleteByConfiguration(cleanUpConfiguration);
		parameterRepository.save(new ParameterImpl(cleanUpConfiguration, Key.DaysBack, "30"));
	}

	private void saveEndOfDayConfiguration(final Cycle nonWorkingDayCycle, final Cycle workingDyCycle) {
		final var endOfBayConfiguration = configurationRepository.save(new ConfigurationImpl(1L, RuleKey.EndOfDay, "EndofDayBatch"));
		parameterRepository.deleteByConfiguration(endOfBayConfiguration);
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MaxSunUpTime, "00:01"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MinSunDownTime, "17:15"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.UpTime, "07:15"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.SunUpDownType, TwilightType.Mathematical.name()));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "07:15", nonWorkingDayCycle));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "05:30", workingDyCycle));
	}

	@Transactional
	@Override
	public Map<Key, ? extends Object> parameters(final RuleKey ruleKey, final Cycle cycle) {
		Assert.notNull(ruleKey, "Key is required");
		Assert.notNull(cycle, "Cycle is required");
		final var configuration = configurationRepository.findByKey(ruleKey).orElseThrow(() -> new EntityNotFoundException(String.format(CYCLE_KEY_NOT_FOUND_MESSAGE_PATTERN, ruleKey)));

		final Collection<? extends Parameter> parameters = parameterRepository.findByConfiguration(configuration);

		final Map<Key, Parameter> cycleParameters = parameters.stream().filter(parameter -> parameter instanceof CycleParameter).filter(parameter -> ((CycleParameter) parameter).cycle().equals(cycle))
				.collect(Collectors.toMap(Parameter::key, parameter -> parameter));

		final Collection<Parameter> globalParameters = parameters.stream().filter(parameter -> !cycleParameters.containsKey(parameter.key())).collect(Collectors.toList());

		final Map<Key, ? extends Object> results = Stream.concat(cycleParameters.values().stream(), globalParameters.stream())
				.map(parameter -> new SimpleImmutableEntry<>(parameter.key(), conversionService.convert(parameter.value(), parameter.key().type())))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return results;

	}

}
