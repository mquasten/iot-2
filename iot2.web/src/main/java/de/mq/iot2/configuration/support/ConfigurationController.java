package de.mq.iot2.configuration.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class ConfigurationController implements ErrorController {
	static final String ERROR_VIEW_NAME = "error";
	static final String CONFIGURATION_MODEL_AND_VIEW_NAME = "configuration";
	static final String CONFIGURATION_LIST_NAME = "configurations";
	private static final String CONFIGURATION_ID_PARAMETER_NAME = "configurationId";
	static final String REDIRECT_CONFIGURATION_PATTERN = "redirect:" + CONFIGURATION_MODEL_AND_VIEW_NAME + "?" + CONFIGURATION_ID_PARAMETER_NAME + "=%s";
	static final String CONFIGURATION_ID_REQUIRED_MESSAGE = "ConfigurationId is required.";

	private final ConfigurationService configurationService;
	private final ModelMapper<Parameter, ParameterModel> parameterMapper;
	private final ModelMapper<Configuration, ConfigurationModel> configurationMapper;

	ConfigurationController(final ConfigurationService configurationService, final ModelMapper<Configuration, ConfigurationModel> configurationMapper,
			final ModelMapper<Parameter, ParameterModel> parameterMapper) {
		this.configurationService = configurationService;
		this.configurationMapper = configurationMapper;
		this.parameterMapper = parameterMapper;
	}

	@GetMapping(value = "/configuration")
	String configuration(final Model model, @RequestParam(name = CONFIGURATION_ID_PARAMETER_NAME, required = false) final String configurationId) {

		model.addAllAttributes(initModel(Optional.ofNullable(configurationId)));

		return CONFIGURATION_MODEL_AND_VIEW_NAME;
	}

	@PostMapping(value = "/searchConfiguration")
	String search(@ModelAttribute(CONFIGURATION_MODEL_AND_VIEW_NAME) @Valid final ConfigurationModel configurationModel, final BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return CONFIGURATION_MODEL_AND_VIEW_NAME;
		}

		return String.format(REDIRECT_CONFIGURATION_PATTERN, configurationModel.getId());
	}

	private Map<String, Object> initModel(final Optional<String> configurationId) {
		final Map<String, Object> attributes = new HashMap<>();
		final Map<String, ConfigurationModel> configurationMap = configurationService.configurations().stream().map(configuration -> configurationMapper.toWeb(configuration))
				.collect(Collectors.toMap(ConfigurationModel::getId, Function.identity()));

		final Collection<ConfigurationModel> configurations = configurationMap.values().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).collect(Collectors.toList());
		attributes.put(CONFIGURATION_LIST_NAME, configurations);

		configurationId.ifPresentOrElse(
				id -> attributes.put(CONFIGURATION_MODEL_AND_VIEW_NAME,
						configurationMap.containsKey(id) ? mapParameterInto(configurationMap.get(id)) : firstConfigurationIfExistsOrNew(configurations)),
				() -> attributes.put(CONFIGURATION_MODEL_AND_VIEW_NAME, firstConfigurationIfExistsOrNew(configurations)));

		return Collections.unmodifiableMap(attributes);
	}

	private ConfigurationModel firstConfigurationIfExistsOrNew(final Collection<ConfigurationModel> configurations) {

		return configurations.stream().findFirst().map(x -> mapParameterInto(x)).orElse(new ConfigurationModel());

	}

	private ConfigurationModel mapParameterInto(final ConfigurationModel configuration) {
		Assert.notNull(configuration, "Configuration required");
		Assert.hasText(configuration.getId(), CONFIGURATION_ID_REQUIRED_MESSAGE);
		configuration.setParameters(parameterMapper.toWeb(configurationService.parameters(configuration.getId())));
		return configuration;
	}

	@RequestMapping("/error")
	public String handleError() {
		return ERROR_VIEW_NAME;
	}

}
