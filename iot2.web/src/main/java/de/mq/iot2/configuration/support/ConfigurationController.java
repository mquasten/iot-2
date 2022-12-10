package de.mq.iot2.configuration.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import jakarta.validation.Valid;

@Controller
class ConfigurationController implements ErrorController {

	private static final String CONFIGURATION_ID_REQUIRED_MESSAGE = "ConfigurationId is required.";

	private final ConfigurationService configurationService;

	private final Converter<Parameter, ParameterModel> parameterConverter;

	private final Converter<Configuration, ConfigurationModel> configurationConverter;

	ConfigurationController(final ConfigurationService configurationService, final Converter<Configuration, ConfigurationModel> configurationConverter,
			final Converter<Parameter, ParameterModel> parameterConverter) {
		this.configurationService = configurationService;
		this.configurationConverter = configurationConverter;
		this.parameterConverter = parameterConverter;
	}

	@GetMapping("/configuration")
	String configuration(final Model model) {
		model.addAllAttributes(initModel(Optional.empty()));
		return "configuration";
	}

	@PostMapping(value = "/search")
	String search(@ModelAttribute("configuration") @Valid final ConfigurationModel configurationModel, final BindingResult bindingResult, final Model model) {
		Assert.hasText(configurationModel.getId(), CONFIGURATION_ID_REQUIRED_MESSAGE);
		model.addAllAttributes(initModel(Optional.of(configurationModel.getId())));
		return "configuration";
	}

	private Map<String, Object> initModel(final Optional<String> configurationId) {

		final Map<String, Object> attributes = new HashMap<>();
		final Map<String, ConfigurationModel> configurationMap = configurationService.configurations().stream().map(configuration -> configurationConverter.convert(configuration))
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
		configuration.setParameters(configurationService.parameters(configuration.getId()).stream().map(parameter -> parameterConverter.convert(parameter)).collect(Collectors.toList()));
		return configuration;
	}

	@PostMapping(value = "/showParameter")
	String showParameter(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		model.addAttribute("parameter", parameterConverter.convert(configurationService.parameter(parameterModel.getId())));
		return "parameter";
	}

	@PostMapping(value = "/updateParameter", params = "save")
	String updateParameter(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		Assert.hasText(parameterModel.getConfigurationId(), CONFIGURATION_ID_REQUIRED_MESSAGE);
		model.addAllAttributes(initModel(Optional.of(parameterModel.getConfigurationId())));
		if (bindingResult.hasFieldErrors()) {
			return "parameter";
		}
		
		return "configuration";
	}

	@PostMapping(value = "/updateParameter", params = "cancel")
	String cancelUpdateParameter(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {

		Assert.hasText(parameterModel.getConfigurationId(), CONFIGURATION_ID_REQUIRED_MESSAGE);
		model.addAllAttributes(initModel(Optional.of(parameterModel.getConfigurationId())));
		return "configuration";
	}

	@RequestMapping("/error")
	public String handleError() {
		return "error";
	}

}
