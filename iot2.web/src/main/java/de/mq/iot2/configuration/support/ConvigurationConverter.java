package de.mq.iot2.configuration.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.support.IdUtil;

@Component
class ConvigurationConverter implements Converter<Configuration, ConfigurationModel> {
	
	@Override
	public ConfigurationModel convert(final Configuration configuration) {
		final var  configurationModel = new ConfigurationModel();
		configurationModel.setId(IdUtil.getId(configuration));
		configurationModel.setName(configuration.name());
		return configurationModel;
	}

	

}
