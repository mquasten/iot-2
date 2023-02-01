package de.mq.iot2.sysvars.support;


import java.util.Locale;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.sysvars.SystemVariables;
import jakarta.validation.Valid;



@Controller
class VariableController {
	static final String VARIABLE_MODEL_AND_VIEW_NAME = "variable";
	
	private final ModelMapper<SystemVariables, VariableModel> variableMapper;
	
	VariableController(final ModelMapper<SystemVariables, VariableModel> variableMapper) {
		this.variableMapper = variableMapper;
	}

	@GetMapping(value = "/variable")
	String variable(final Model model, @RequestParam(name = "readSystemVariables", required = false) final boolean readSystemVariables, final Locale locale) {
		
		final VariableModel variableModel = variableMapper.toWeb(new SystemVariables());
		variableModel.setLocale(locale);
		model.addAttribute(VARIABLE_MODEL_AND_VIEW_NAME, variableModel);
		return VARIABLE_MODEL_AND_VIEW_NAME;
	}
	
	@PostMapping(value = "/updateTimer", params="today" )
	String updateTimerToday(@ModelAttribute(VARIABLE_MODEL_AND_VIEW_NAME) @Valid final VariableModel variableModel, final BindingResult bindingResult) {
		System.out.println("today");
		System.out.println(variableModel.getMaxTemperatureToday());
		System.out.println(variableModel.getSunUpToday());
		System.out.println(variableModel.getSunDownToday());
		
		final TimerModel timerModel = new TimerModel();
		
		timerModel.setSecondUp(variableModel.getSunUpToday());
		timerModel.setDown(variableModel.getSunDownToday());
		
	System.out.println(Base64Utils.encodeToString(SerializationUtils.serialize(new String[] {"xxx" , null , "a" , "b"})));
		return "redirect:" +  VARIABLE_MODEL_AND_VIEW_NAME;
	}
	
	@PostMapping(value = "/updateTimer", params="tomorrow" )
	String updateTimerTomorrow(@ModelAttribute(VARIABLE_MODEL_AND_VIEW_NAME) @Valid final VariableModel variableModel, final BindingResult bindingResult) {
		System.out.println("tomorrow");
		System.out.println(variableModel.getMaxTemperatureTomorrow());
		System.out.println(variableModel.getSunUpTomorrow());
		System.out.println(variableModel.getSunDownTomorrow());


		return "redirect:" +  VARIABLE_MODEL_AND_VIEW_NAME;
	}

}
