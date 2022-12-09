package de.mq.iot2.configuration.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.IdUtil;

@Component
class ParameterConverter implements Converter<Parameter, ParameterModel> {

	@Override
	public ParameterModel convert(final Parameter parameter) {
		final var parameterModel= new ParameterModel();
		parameterModel.setId(IdUtil.getId(parameter));
		parameterModel.setName(parameter.key().name());
		parameterModel.setValue(parameter.value());
		parameterModel.setConfiguration(parameter.configuration().name());
		parameterModel.setConfigurationId(IdUtil.getId(parameter.configuration()));
		if (parameter instanceof CycleParameter) {
			parameterModel.setCycle(((CycleParameter) parameter).cycle().name());
		}
		return parameterModel;
	}

	

}
