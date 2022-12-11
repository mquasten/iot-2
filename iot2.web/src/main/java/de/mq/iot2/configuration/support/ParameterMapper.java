package de.mq.iot2.configuration.support;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Id;

@Component
class ParameterMapper implements ModelMapper<Parameter, ParameterModel> {

	private final ParameterRepository parameterRepository;
	
	ParameterMapper(final ParameterRepository parameterRepository) {
		this.parameterRepository = parameterRepository;
	}

	@Override
	public ParameterModel toWeb(final Parameter parameter) {
		final var parameterModel= new ParameterModel();
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
		Assert.hasText(id, "Id is required.");
		return parameterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Parameter with id %s not found.", id)));
		
	}
	
	public Parameter toDomain(final ParameterModel parameterModel ) {
		
		final Parameter existingParameter = toDomain(parameterModel.getId());
		
		if (existingParameter instanceof CycleParameter) {
			final var parameter =new CycleParameterImpl(existingParameter.configuration(), existingParameter.key(), parameterModel.getValue(), ((CycleParameter)existingParameter).cycle());
			assignId(parameter, parameterModel.getId());
			return parameter;
		} else {
			final var parameter  = new ParameterImpl(existingParameter.configuration(), existingParameter.key(), parameterModel.getValue());
			assignId(parameter, parameterModel.getId());
			return parameter;
		}
	}

	private void assignId(final Parameter parameter, final String id) {
		ReflectionUtils.doWithFields(parameter.getClass(), field -> {
			field.setAccessible(true);
		    ReflectionUtils.setField(field, parameter, id) ;	
		}, field -> field.isAnnotationPresent(Id.class));
	}

	

}
