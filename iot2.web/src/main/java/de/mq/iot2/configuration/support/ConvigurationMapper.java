package de.mq.iot2.configuration.support;

import org.springframework.stereotype.Component;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

@Component
class ConvigurationMapper implements ModelMapper<Configuration, ConfigurationModel> {
	
	@Override
	public ConfigurationModel toWeb(final Configuration configuration) {
		final var  configurationModel = new ConfigurationModel();
		configurationModel.setId(IdUtil.getId(configuration));
		configurationModel.setName(configuration.name());
		return configurationModel;
	}

	
	

}
