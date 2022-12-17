package de.mq.iot2.configuration.support;

import static de.mq.iot2.configuration.support.ConfigurationController.CONFIGURATION_MODEL_AND_VIEW_NAME;
import static de.mq.iot2.configuration.support.ParameterController.CONFIGURATION_ID;
import static de.mq.iot2.configuration.support.ParameterController.PARAMETER_MODEL_AND_VIEW_NAME;
import static de.mq.iot2.support.Constants.FORWARD_VIEW_PATTERN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.ModelMapper;

class ParameterControllerTest {
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<Parameter, ParameterModel> parameterMapper = mock(ModelMapper.class);

	private ParameterController parameterController = new ParameterController(configurationService, parameterMapper);

	private final Model model = new ExtendedModelMap();
	private final ParameterModel parameterModel = mock(ParameterModel.class);
	private final Parameter parameter = mock(Parameter.class);

	@Test
	void showParameter() {
		final var parameterModel = newParameterModelWithId();
		when(parameterMapper.toWeb(parameterModel.getId())).thenReturn(this.parameterModel);

		assertEquals(PARAMETER_MODEL_AND_VIEW_NAME, parameterController.showParameter(parameterModel, model));

		assertEquals(this.parameterModel, model.getAttribute(PARAMETER_MODEL_AND_VIEW_NAME));
	}

	private ParameterModel newParameterModelWithId() {
		final var parameterModel = new ParameterModel();
		parameterModel.setId(UUID.randomUUID().toString());
		return parameterModel;
	}

	@Test
	void updateParameter() {
		final var parameterModel = newParameterModelWithIdAndConfigurationId();
		when(parameterMapper.toDomain(parameterModel)).thenReturn(parameter);

		assertEquals(String.format(FORWARD_VIEW_PATTERN, CONFIGURATION_MODEL_AND_VIEW_NAME), parameterController.updateParameter(parameterModel, mock(BindingResult.class), model));

		assertEquals(parameterModel.getConfigurationId(), model.getAttribute(CONFIGURATION_ID));
		verify(configurationService).save(parameter);
	}

	private ParameterModel newParameterModelWithIdAndConfigurationId() {
		final var parameterModel = newParameterModelWithId();
		parameterModel.setConfigurationId(UUID.randomUUID().toString());
		return parameterModel;
	}

	@Test
	void updateParameterBindingErrors() {
		final var parameterModel = newParameterModelWithIdAndConfigurationId();

		final var bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		assertEquals(PARAMETER_MODEL_AND_VIEW_NAME, parameterController.updateParameter(parameterModel, bindingResult, model));

		verify(configurationService, never()).save(Mockito.any());
	}

	@Test
	void cancelUpdateParameter() {
		final ParameterModel parameterModel = newParameterModelWithIdAndConfigurationId();

		assertEquals(String.format(FORWARD_VIEW_PATTERN, CONFIGURATION_MODEL_AND_VIEW_NAME), parameterController.cancelUpdateParameter(parameterModel, model));
		assertEquals(parameterModel.getConfigurationId(), model.getAttribute(CONFIGURATION_ID));
	}

}
