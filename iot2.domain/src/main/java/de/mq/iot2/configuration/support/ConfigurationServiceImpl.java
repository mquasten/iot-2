package de.mq.iot2.configuration.support;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;

@Service
class ConfigurationServiceImpl implements ConfigurationService {

	private final ConfigurationRepository configurationRepository;

	ConfigurationServiceImpl(ConfigurationRepository configurationRepository) {
		this.configurationRepository = configurationRepository;
	}

	@Override
	@Transactional
	public void createDefaultConfigurationsAndParameters() {

		final var configuration = new ConfigurationImpl(1L, RuleKey.EndOfDay, "EndofDayBatch");
		configurationRepository.save(configuration);

	}

}
