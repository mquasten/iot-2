package de.mq.iot2.configuration.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import jakarta.validation.Valid;

@Controller
class ParameterController {
	
	private final  ConfigurationService configurationService;
	private final Converter<Parameter, ParameterModel> parameterConverter;
	
	public ParameterController(final  ConfigurationService configurationService, final Converter<Parameter, ParameterModel> parameterConverter) {
		this.configurationService=configurationService;
		this.parameterConverter = parameterConverter;
	}

	@GetMapping("/parameter")
	String parameter(final Model model, @RequestParam(value = "parameterId", required = true) String parameterId) {
		
		final var parameterModel = parameterConverter.convert(configurationService.parameter(parameterId));
		model.addAttribute("parameter", parameterModel );
		return "parameter";
		
	}
	
	@PostMapping(value = "/updateParameter")
	String search(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		
		System.out.println(parameterModel.getConfiguration());

		System.out.println(parameterModel.getId());
		
		System.out.println(parameterModel.getValue());
		
		System.out.println(parameterModel.getName());
		
		
		return String.format("redirect:configuration?configurationId=%s", parameterModel.getConfigurationId());
	}

}
