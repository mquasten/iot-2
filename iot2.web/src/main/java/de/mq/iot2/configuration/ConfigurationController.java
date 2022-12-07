package de.mq.iot2.configuration;


import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.support.IdUtil;
import jakarta.validation.Valid;


@Controller
class ConfigurationController {
	
	private final  ConfigurationService configurationService;
	
	ConfigurationController(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@GetMapping("/configuration")
	String configuration(final Model model, @RequestParam(value = "configurationId", required = false, defaultValue = "") String configurationId) {
		System.out.println("/configuration?" + configurationId);
		final var configurations = configurationService.configurations().stream().collect(Collectors.toMap(configuration -> IdUtil.getId(configuration), Configuration::name));
		//final var configurations = Map.of("1", "End of Day", "2", "Cleanup");
				
				
				
		final ConfigurationModel configurationModel= new ConfigurationModel();
		if (configurations.containsKey(configurationId)) {
			configurationModel.setConfigurationId(configurationId);
			configurationModel.setName(configurations.get(configurationId));
			configurationService.parameters(configurationId);
			configurationModel.setParameters(parameters(configurationId));
		}
		
		model.addAttribute("configuration", configurationModel );
		
		model.addAttribute("configurations", configurations.entrySet());
		return "configuration";
	}

	private Collection<ParameterModel> parameters(final String configurationId) {
		final Collection<ParameterModel> parameters = new ArrayList<>();
		for (final Parameter parameter : configurationService.parameters(configurationId)) {
			ParameterModel parameterModel= new ParameterModel();
			parameterModel.setId(IdUtil.getId(parameter));
			parameterModel.setName(parameter.key().name());
			parameterModel.setValue(parameter.value());
			parameters.add(parameterModel);
			
		}
		return parameters;
	}

	@PostMapping(value = "/search")
	String search(@ModelAttribute("configuration") @Valid final ConfigurationModel configurationModel, final BindingResult bindingResult, final Model model) {
		System.out.println("/search");
		return String.format("redirect:configuration?configurationId=%s", configurationModel.getConfigurationId());
	}
	
	@PostMapping(value = "/updateParameter")
	String editParameter(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		System.out.println("/updateParameter: " + parameterModel.getId());
		return String.format("redirect:configuration?configurationId=%s", parameterModel.getConfigurationId());
	}

}
