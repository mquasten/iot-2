package de.mq.iot2.configuration.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class ParameterController {
	
	private final ConfigurationService configurationService;
	private final ModelMapper<Parameter, ParameterModel> parameterMapper;
	
	ParameterController(final ConfigurationService configurationService, final ModelMapper<Parameter, ParameterModel> parameterMapper) {
		this.configurationService = configurationService;
		this.parameterMapper = parameterMapper;
	}
	
	@PostMapping(value = "/showParameter")
	String showParameter(@ModelAttribute("parameter") final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		model.addAttribute("parameter", parameterMapper.toWeb(parameterModel.getId()));
		return "parameter";
	}

	@PostMapping(value = "/updateParameter", params = "save")
	String updateParameter(@ModelAttribute("parameter") @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		Assert.hasText(parameterModel.getConfigurationId(), ConfigurationController.CONFIGURATION_ID_REQUIRED_MESSAGE);
		Assert.hasText(parameterModel.getId(), "Id is required.");

		if (bindingResult.hasErrors()) {
			return "parameter";
		}

		configurationService.save(parameterMapper.toDomain(parameterModel));
		model.addAttribute("configurationId" , parameterModel.getConfigurationId());
		return "forward:configuration";
	}

	@PostMapping(value = "/updateParameter", params = "cancel")
	String cancelUpdateParameter(@ModelAttribute("parameter") final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		Assert.hasText(parameterModel.getConfigurationId(), ConfigurationController.CONFIGURATION_ID_REQUIRED_MESSAGE);
		model.addAttribute("configurationId" , parameterModel.getConfigurationId());
		return "forward:configuration";
	}


}
