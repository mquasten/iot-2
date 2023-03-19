package de.mq.iot2.configuration.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
	static final String WRONG_NUMBER_OF_COLUMNS_MESSAGE = "Wrong number of Columns in line %s.";
	private final ConfigurationRepository configurationRepository;
	private final ParameterRepository parameterRepository;
	private final CycleRepository cycleRepository;
	private final ConversionService conversionService;
	private  final Converter<Pair<Parameter, Boolean>, String[]> parameterCsvConverter; 
	private  final Converter<Pair<String[], Pair<Map<String, Configuration>, Map<String, Cycle>>>,Parameter> arrayCsvConverter;
	private final String csvDelimiter;
	ConfigurationServiceImpl(ConfigurationRepository configurationRepository, final ParameterRepository parameterRepository, final CycleRepository cycleRepository, final ConversionService conversionService, final Converter<Pair<Parameter, Boolean>, String[]> parameterCsvConverter, Converter<Pair<String[], Pair<Map<String, Configuration>, Map<String, Cycle>>>, Parameter> arrayCsvConverter ,@Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.configurationRepository = configurationRepository;
		this.parameterRepository = parameterRepository;
		this.cycleRepository = cycleRepository;
		this.conversionService = conversionService;
		this.parameterCsvConverter= parameterCsvConverter;
		this.arrayCsvConverter=arrayCsvConverter;
		this.csvDelimiter=csvDelimiter;
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
			final Collection<String> configurationsIdsProcessed = new HashSet<>();
			parameterRepository.findAll().stream().sorted((p1, p2) -> {
				final int compareConfigurations = p1.configuration().name().compareTo(p2.configuration().name());
				if (compareConfigurations != 0) {
					return compareConfigurations;
				}
			
				final int compareParameters = p1.key().name().compareTo(p2.key().name());
				if ( compareParameters != 0) {
					return compareParameters;
				}
				return IdUtil.getId(p1).compareTo(IdUtil.getId(p2));

			}).forEach(parameter -> {
				final String configurationId = IdUtil.getId(parameter.configuration());
				writer.println(StringUtils.arrayToDelimitedString(parameterCsvConverter.convert(Pair.of(parameter, configurationsIdsProcessed.contains(configurationId) )), csvDelimiter));
				configurationsIdsProcessed.add(configurationId);
			});
		}

			
	}

	@Override
	@Transactional
	public void importCsv(final InputStream is) throws IOException {
		try (final InputStreamReader streamReader = new InputStreamReader(is); final BufferedReader reader = new BufferedReader(streamReader)) {
			final Map<String, Configuration> configurations = new HashMap<>();
			final Map<String, Cycle> cycles = cycleRepository.findAll().stream().collect(Collectors.toMap(IdUtil::getId, Function.identity()));		
			
			for (int i = 1; reader.ready(); i++) {
				final String pattern = String.format("[%s](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", csvDelimiter);
				final String line = reader.readLine();

				final String[] cols = List.of(line.split(pattern, -1)).stream().map(col -> StringUtils.trimTrailingCharacter(StringUtils.trimLeadingCharacter(col.strip(), '"'), '"').strip())
						.toArray(size -> new String[size]);

				
				
				Assert.isTrue(cols.length == 7, String.format(WRONG_NUMBER_OF_COLUMNS_MESSAGE, i));

				final Parameter parameter = arrayCsvConverter.convert(Pair.of(cols, Pair.of(configurations, cycles)));
				
				
				if (!configurations.containsKey(IdUtil.getId(parameter.configuration()))) {
					configurations.put(IdUtil.getId(parameter.configuration()), parameter.configuration());
					configurationRepository.save(parameter.configuration());
				}

				parameterRepository.save(parameter);
				i++;
			}
		}
	}

}
