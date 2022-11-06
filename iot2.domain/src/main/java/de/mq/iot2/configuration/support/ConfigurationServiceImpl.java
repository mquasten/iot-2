package de.mq.iot2.configuration.support;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleRepository;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;

@Service
class ConfigurationServiceImpl implements ConfigurationService {

	private static final long WORKING_DAY_CYCLE_ID = 2L;
	private static final long NON_WORKING_DAY_CYCLE_ID = 1L;
	private final ConfigurationRepository configurationRepository;
	private final ParameterRepository parameterRepository;
	private final CycleRepository cycleRepository;

	ConfigurationServiceImpl(ConfigurationRepository configurationRepository, final ParameterRepository parameterRepository, final CycleRepository cycleRepository) {
		this.configurationRepository = configurationRepository;
		this.parameterRepository = parameterRepository;
		this.cycleRepository = cycleRepository;
	}

	@Override
	@Transactional
	public void createDefaultConfigurationsAndParameters() {

		final var nonWorkingDayCycle = cycleRepository.findById(IdUtil.id(NON_WORKING_DAY_CYCLE_ID)).orElseThrow(() -> new EntityNotFoundException("Non Workingday Cycle not found."));
		final var workingDayCycle = cycleRepository.findById(IdUtil.id(WORKING_DAY_CYCLE_ID)).orElseThrow(() -> new EntityNotFoundException("Workingday Cycle not found."));
		saveEndOfDayConfiguration(nonWorkingDayCycle, workingDayCycle);
		saveCleanUpConfiguration();
	}

	private void saveCleanUpConfiguration() {
		final var cleanUpConfiguration = configurationRepository.save(new ConfigurationImpl(2L, RuleKey.CleanUp, "CleanUpBatch"));
		parameterRepository.deleteByConfiguration(cleanUpConfiguration);
		parameterRepository.save(new ParameterImpl(cleanUpConfiguration, Key.DaysBack, "30"));
	}

	private void saveEndOfDayConfiguration(final Cycle nonWorkingDayCycle, final Cycle workingDyCycle) {
		final var endOfBayConfiguration = configurationRepository.save(new ConfigurationImpl(1L, RuleKey.EndOfDay, "EndofDayBatch"));
		parameterRepository.deleteByConfiguration(endOfBayConfiguration);
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MaxSunUpTime, "00:01"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.MinSunDownTime, "17:15"));
		parameterRepository.save(new ParameterImpl(endOfBayConfiguration, Key.UpTime, "07:15"));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "07:15", nonWorkingDayCycle));
		parameterRepository.save(new CycleParameterImpl(endOfBayConfiguration, Key.UpTime, "05:30", workingDyCycle));
	}

	

}
