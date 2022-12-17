package de.mq.iot2.configuration.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.support.Constants;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class ParameterController {
	static final String PARAMETER_MODEL_AND_VIEW_NAME =  "parameter";
	static final String CONFIGURATION_ID = "configurationId";
	private final ConfigurationService configurationService;
	private final ModelMapper<Parameter, ParameterModel> parameterMapper;
	
	ParameterController(final ConfigurationService configurationService, final ModelMapper<Parameter, ParameterModel> parameterMapper) {
		this.configurationService = configurationService;
		this.parameterMapper = parameterMapper;
	}

	@PostMapping(value = "/showParameter")
	String showParameter(@ModelAttribute(PARAMETER_MODEL_AND_VIEW_NAME) final ParameterModel parameterModel, final Model model) {
		model.addAttribute(PARAMETER_MODEL_AND_VIEW_NAME, parameterMapper.toWeb(parameterModel.getId()));
		return PARAMETER_MODEL_AND_VIEW_NAME;
	}

	@PostMapping(value = "/updateParameter", params = "save")
	String updateParameter(@ModelAttribute(PARAMETER_MODEL_AND_VIEW_NAME) @Valid final ParameterModel parameterModel, final BindingResult bindingResult, final Model model) {
		Assert.hasText(parameterModel.getConfigurationId(), ConfigurationController.CONFIGURATION_ID_REQUIRED_MESSAGE);
		Assert.hasText(parameterModel.getId(), "Id is required.");

		if (bindingResult.hasErrors()) {
			return PARAMETER_MODEL_AND_VIEW_NAME;
		}

		configurationService.save(parameterMapper.toDomain(parameterModel));
		model.addAttribute(CONFIGURATION_ID, parameterModel.getConfigurationId());
		return forwardConfiguration();
	}

	private String forwardConfiguration() {
		return String.format(Constants.FORWARD_VIEW_PATTERN, ConfigurationController.CONFIGURATION_MODEL_AND_VIEW_NAME);
	}

	@PostMapping(value = "/updateParameter", params = "cancel")
	String cancelUpdateParameter(@ModelAttribute("parameter") final ParameterModel parameterModel, final Model model) {
		Assert.hasText(parameterModel.getConfigurationId(), ConfigurationController.CONFIGURATION_ID_REQUIRED_MESSAGE);
		model.addAttribute(CONFIGURATION_ID, parameterModel.getConfigurationId());
		return  forwardConfiguration();
	}

}
