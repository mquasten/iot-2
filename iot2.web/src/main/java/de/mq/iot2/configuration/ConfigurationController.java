package de.mq.iot2.configuration;


import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
class ConfigurationController {
	
	@GetMapping("/configuration")
	String configuration(final Model model, @RequestParam(value = "configurationId", required = false) String configurationId) {
		System.out.println("/configuration?" + configurationId);
		final var configurations = Map.of("1", "End of Day", "2", "Cleanup");
				
				
				
		ConfigurationModel configurationModel= new ConfigurationModel();
		if (configurations.containsKey(configurationId)) {
			configurationModel.setConfigurationId(configurationId);
			configurationModel.setName(configurations.get(configurationId));
			ParameterModel parameter1= new ParameterModel();
			parameter1.setId("1");
			parameter1.setName("DaysBack");
			parameter1.setValue("30");
			configurationModel.setParameters(Arrays.asList(parameter1));
		}
		
		model.addAttribute("configuration", configurationModel );
		
		model.addAttribute("configurations", configurations.entrySet());
		return "configuration";
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
