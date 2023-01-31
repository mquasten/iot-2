package de.mq.iot2.sysvars.support;


import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.sysvars.SystemVariables;



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

}
