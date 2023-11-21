package de.mq.iot2.configuration.support;

import static de.mq.iot2.configuration.support.ConfigurationController.CONFIGURATION_LIST_NAME;
import static de.mq.iot2.configuration.support.ConfigurationController.CONFIGURATION_MODEL_AND_VIEW_NAME;
import static de.mq.iot2.configuration.support.ConfigurationController.ERROR_VIEW_NAME;
import static de.mq.iot2.configuration.support.ConfigurationController.REDIRECT_CONFIGURATION_PATTERN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.ModelMapper;

class ConfigurationControllerTest {

	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<Configuration, ConfigurationModel> configurationMapper = mock(ModelMapper.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<Parameter, ParameterModel> parameterMapper = mock(ModelMapper.class);

	private final ConfigurationController configurationController = new ConfigurationController(configurationService, configurationMapper, parameterMapper);

	private final Model model = new ExtendedModelMap();

	private final ConfigurationModel cleanupConfigurationModel = newConfigurationModelWithRandomId(RuleKey.CleanUp.name());
	private final ConfigurationModel endOfDayConfigurationModel = newConfigurationModelWithRandomId(RuleKey.EndOfDay.name());

	private final Collection<ParameterModel> cleanupParameterModelList = List.of(new ParameterModel());
	private final Collection<ParameterModel> endOfDayParameterModelList = List.of(new ParameterModel());

	private final BindingResult bindingResults = mock(BindingResult.class);

	private void setup() {
		final var cleanupConfiguration = new ConfigurationImpl(RuleKey.CleanUp, RuleKey.CleanUp.name());
		final var endOfDayConfiguration = new ConfigurationImpl(RuleKey.EndOfDay, RuleKey.EndOfDay.name());
		when(configurationService.configurations()).thenReturn(List.of(cleanupConfiguration, endOfDayConfiguration));
		when(configurationMapper.toWeb(cleanupConfiguration)).thenReturn(cleanupConfigurationModel);
		when(configurationMapper.toWeb(endOfDayConfiguration)).thenReturn(endOfDayConfigurationModel);
		final Collection<Parameter> cleanupParameterList = List.of(new ParameterImpl(cleanupConfiguration, Key.DaysBack, "30"));
		when(configurationService.parameters(cleanupConfigurationModel.getId())).thenReturn(cleanupParameterList);
		final Collection<Parameter> endOfDayParameterList = List.of(new ParameterImpl(endOfDayConfiguration, Key.UpTime, "05:30"));
		when(configurationService.parameters(endOfDayConfigurationModel.getId())).thenReturn(endOfDayParameterList);
		when(parameterMapper.toWeb(cleanupParameterList)).thenReturn(cleanupParameterModelList);
		when(parameterMapper.toWeb(endOfDayParameterList)).thenReturn(endOfDayParameterModelList);
	}

	private ConfigurationModel newConfigurationModelWithRandomId(final String name) {
		final var configurationModel = new ConfigurationModel();
		configurationModel.setId(UUID.randomUUID().toString());
		configurationModel.setName(name);
		return configurationModel;
	}

	@Test
	void configuration() {
		setup();

		assertEquals(CONFIGURATION_MODEL_AND_VIEW_NAME, configurationController.configuration(model, null));

		final List<?> configurations = (List<?>) model.getAttribute(CONFIGURATION_LIST_NAME);
		assertEquals(2, configurations.size());
		assertEquals(cleanupConfigurationModel, configurations.get(0));
		assertEquals(endOfDayConfigurationModel, configurations.get(1));
		final ConfigurationModel configuration = (ConfigurationModel) model.getAttribute(CONFIGURATION_MODEL_AND_VIEW_NAME);
		assertEquals(cleanupConfigurationModel, configuration);
		assertEquals(cleanupParameterModelList, configuration.getParameters());
	}

	@Test
	void configurationWithId() {
		setup();

		assertEquals(CONFIGURATION_MODEL_AND_VIEW_NAME, configurationController.configuration(model, endOfDayConfigurationModel.getId()));

		final List<?> configurations = (List<?>) model.getAttribute(CONFIGURATION_LIST_NAME);
		assertEquals(2, configurations.size());
		assertEquals(cleanupConfigurationModel, configurations.get(0));
		assertEquals(endOfDayConfigurationModel, configurations.get(1));
		final ConfigurationModel configuration = (ConfigurationModel) model.getAttribute(CONFIGURATION_MODEL_AND_VIEW_NAME);
		assertEquals(endOfDayConfigurationModel, configuration);
		assertEquals(endOfDayParameterModelList, configuration.getParameters());
	}

	@Test
	void configurationIdNotFound() {
		setup();

		assertEquals(CONFIGURATION_MODEL_AND_VIEW_NAME, configurationController.configuration(model, UUID.randomUUID().toString()));

		final List<?> configurations = (List<?>) model.getAttribute(CONFIGURATION_LIST_NAME);
		assertEquals(2, configurations.size());
		assertEquals(cleanupConfigurationModel, configurations.get(0));
		assertEquals(endOfDayConfigurationModel, configurations.get(1));
		final ConfigurationModel configuration = (ConfigurationModel) model.getAttribute(CONFIGURATION_MODEL_AND_VIEW_NAME);
		assertEquals(cleanupConfigurationModel, configuration);
		assertEquals(cleanupParameterModelList, configuration.getParameters());
	}

	@Test
	void configurationEmptyDatabase() {
		assertEquals(CONFIGURATION_MODEL_AND_VIEW_NAME, configurationController.configuration(model, null));

		final List<?> configurations = (List<?>) model.getAttribute(CONFIGURATION_LIST_NAME);
		assertEquals(0, configurations.size());
		final ConfigurationModel configuration = (ConfigurationModel) model.getAttribute(CONFIGURATION_MODEL_AND_VIEW_NAME);
		assertNull(configuration.getId());
		assertEquals(0, configuration.getParameters().size());
	}

	@Test
	void search() {
		assertEquals(String.format(REDIRECT_CONFIGURATION_PATTERN, endOfDayConfigurationModel.getId()), configurationController.search(endOfDayConfigurationModel, bindingResults));
	}

	@Test
	void searchBindingErrors() {
		Mockito.when(bindingResults.hasErrors()).thenReturn(true);
		assertEquals(CONFIGURATION_MODEL_AND_VIEW_NAME, configurationController.search(endOfDayConfigurationModel, bindingResults));
	}

	@Test
	void handleError() {
		assertEquals(ERROR_VIEW_NAME, configurationController.handleError());
	}
}
