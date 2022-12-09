package de.mq.iot2.configuration.support;
import java.util.stream.Collectors;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.IdUtil;
import jakarta.validation.Valid;



@Controller
class ConfigurationController  implements ErrorController{
	
	private final  ConfigurationService configurationService;
	
	private final Converter<Parameter, ParameterModel> parameterConverter;
	
	ConfigurationController(final ConfigurationService configurationService, final Converter<Parameter, ParameterModel> parameterConverter) {
		this.configurationService = configurationService;
		this.parameterConverter=parameterConverter;
	}

	
	
	
	@GetMapping("/configuration")
	String configuration(final Model model, @RequestParam(value = "configurationId", required = false, defaultValue = "") String configurationId) {
		final var configurations = configurationService.configurations().stream().collect(Collectors.toMap(configuration -> IdUtil.getId(configuration), Configuration::name));		
		final ConfigurationModel configurationModel= new ConfigurationModel();
		if (configurations.containsKey(configurationId)) {
			configurationModel.setId(configurationId);
			configurationModel.setName(configurations.get(configurationId));
			configurationModel.setParameters(configurationService.parameters(configurationId).stream().map(parameter -> parameterConverter.convert(parameter)).collect(Collectors.toList()));
			
		} 
		model.addAttribute("configuration", configurationModel );
		model.addAttribute("configurations", configurations.entrySet());
		
		return "configuration";
	}




	@PostMapping(value = "/search")
	String search(@ModelAttribute("configuration") @Valid final ConfigurationModel configurationModel, final BindingResult bindingResult, final Model model) {
		return String.format("redirect:configuration?configurationId=%s", configurationModel.getId());
	}
	
	@PostMapping(value = "/showParameter")
	String editParameter(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		System.out.println("/updateParameter: " + parameterModel.getId());
		return String.format("redirect:parameter?parameterId=%s", parameterModel.getId());
	}

	
	@RequestMapping("/error")
    public String handleError() {
        //do something like logging
        return "error";
    } 

}
