package de.mq.iot2.configuration.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import jakarta.persistence.EntityNotFoundException;

@Component
class ParameterMapper implements ModelMapper<Parameter, ParameterModel> {

	static final String PARAMETER_NOT_FOUND_MESSAGE = "Parameter with id %s not found.";
	private final ParameterRepository parameterRepository;
	private final ConversionService conversionService;

	ParameterMapper(final ParameterRepository parameterRepository, final ConversionService conversionService) {
		this.parameterRepository = parameterRepository;
		this.conversionService = conversionService;
	}

	@Override
	public ParameterModel toWeb(final Parameter parameter) {
		Assert.notNull(parameter, "Parameter is required.");
		final var parameterModel = new ParameterModel();
		parameterModel.setId(IdUtil.getId(parameter));
		parameterModel.setName(parameter.key().name());
		parameterModel.setValue(parameter.value());
		parameterModel.setConfiguration(parameter.configuration().name());
		parameterModel.setConfigurationId(IdUtil.getId(parameter.configuration()));
		if (parameter instanceof CycleParameter) {
			parameterModel.setCycle(((CycleParameter) parameter).cycle().name());
			parameterModel.setCycleId(IdUtil.getId(((CycleParameter) parameter).cycle()));
		}
		return parameterModel;
	}

	@Override
	public Parameter toDomain(final String id) {
		idRequiredGuard(id);
		return parameterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(PARAMETER_NOT_FOUND_MESSAGE, id)));

	}

	private void idRequiredGuard(final String id) {
		Assert.hasText(id, "Id is required.");
	}

	public Parameter toDomain(final ParameterModel parameterModel) {
		Assert.notNull(parameterModel, "ParameterModel is required.");
		idRequiredGuard(parameterModel.getId());
		Assert.hasText(parameterModel.getValue(), "Value is required.");
		final Parameter existingParameter = toDomain(parameterModel.getId());
		final var valueAsString = conversionService.convert(conversionService.convert(parameterModel.getValue(), existingParameter.key().type()), String.class);
		if (existingParameter instanceof CycleParameter) {
			final var parameter = new CycleParameterImpl(existingParameter.configuration(), existingParameter.key(), valueAsString, ((CycleParameter) existingParameter).cycle());
			IdUtil.assignId(parameter, parameterModel.getId());
			return parameter;
		} else {
			final var parameter = new ParameterImpl(existingParameter.configuration(), existingParameter.key(), valueAsString);
			IdUtil.assignId(parameter, parameterModel.getId());
			return parameter;
		}
	}

}
