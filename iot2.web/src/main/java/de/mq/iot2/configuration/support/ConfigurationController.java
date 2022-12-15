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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class ConfigurationController implements ErrorController {
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

	@RequestMapping(value = "/configuration", method = { RequestMethod.GET, RequestMethod.POST })
	String configuration(final Model model, @RequestAttribute(name = "configurationId", required = false) final String configurationId) {
		model.addAllAttributes(initModel(Optional.ofNullable(configurationId)));
		return "configuration";
	}

	@PostMapping(value = "/search")
	String search(@ModelAttribute("configuration") @Valid final ConfigurationModel configurationModel, final BindingResult bindingResult, final Model model) {
		Assert.hasText(configurationModel.getId(), CONFIGURATION_ID_REQUIRED_MESSAGE);
		model.addAttribute("configurationId", configurationModel.getId());
		return "forward:configuration";
	}

	private Map<String, Object> initModel(final Optional<String> configurationId) {
		final Map<String, Object> attributes = new HashMap<>();
		final Map<String, ConfigurationModel> configurationMap = configurationService.configurations().stream().map(configuration -> configurationMapper.toWeb(configuration))
				.collect(Collectors.toMap(ConfigurationModel::getId, Function.identity()));

		final Collection<ConfigurationModel> configurations = configurationMap.values().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).collect(Collectors.toList());
		attributes.put("configurations", configurations);

		configurationId.ifPresentOrElse(id -> attributes.put("configuration", mapParameterInto(configurationMap.get(id))), () -> configurations.stream().findFirst()
				.ifPresentOrElse(configuration -> attributes.put("configuration", mapParameterInto(configuration)), () -> attributes.put("configuration", new ConfigurationModel())));

		return Collections.unmodifiableMap(attributes);
	}

	private ConfigurationModel mapParameterInto(final ConfigurationModel configuration) {
		Assert.notNull(configuration, "Configuration required");
		Assert.hasText(configuration.getId(), CONFIGURATION_ID_REQUIRED_MESSAGE);
		configuration.setParameters(parameterMapper.toWeb(configurationService.parameters(configuration.getId())));
		return configuration;
	}

	@RequestMapping("/error")
	public String handleError() {
		return "error";
	}

}
