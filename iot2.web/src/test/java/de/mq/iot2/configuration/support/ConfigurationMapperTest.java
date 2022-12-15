package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

class ConfigurationMapperTest {

	private static final String CONFIGURATION_ID = UUID.randomUUID().toString();
	private final ModelMapper<Configuration, ConfigurationModel> configurationMapper = new ConfigurationMapper();
	private final Configuration configuration = new ConfigurationImpl(RuleKey.EndOfDay, "Configuration-Name");

	@BeforeEach
	void setup() {
		IdUtil.assignId(configuration, CONFIGURATION_ID);
	}

	@Test
	void toWeb() {
		final var result = configurationMapper.toWeb(configuration);

		assertEquals(CONFIGURATION_ID, result.getId());
		assertEquals(configuration.name(), result.getName());
	}

}
