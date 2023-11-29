package de.mq.iot2.protocol.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.support.ModelMapper;
import jakarta.validation.Valid;

@Controller
class ProtocolController {
	
	private static final String BATCHES_ATTRIBUTE_NAME = "batches";
	private static final String PROTOCOL_MODEL_AND_VIEW_NAME = "protocol";
	private static final String BATCH_NAME_PARAMETER_NAME = "batchName";
	static final String REDIRECT_PROTOCOL_PATTERN = "redirect:" + PROTOCOL_MODEL_AND_VIEW_NAME + "?" + BATCH_NAME_PARAMETER_NAME + "=%s";
	
	static final String PROTOCOL_LIST_NAME = "protocols";
	

	private final ProtocolService protocolService;
	
	private final ModelMapper<Protocol,ProtocolModel> protoMapper;
	
	
	ProtocolController(final ProtocolService protocolService, final ModelMapper<Protocol,ProtocolModel> protoMapper) {
		this.protocolService = protocolService;
		this.protoMapper=protoMapper;
	}

	@GetMapping(value = "/protocol")
	String protocol(final Model model, @RequestParam(name = "batchName", required = false) final String batchName) {
		model.addAttribute(BATCHES_ATTRIBUTE_NAME, protocolService.protocolNames());
		ProtocolModel protocol = new ProtocolModel();
		protocol.setName(batchName);
		protocol.setProtocols(protoMapper.toWeb(protocolService.protocols(batchName)));
		model.addAttribute(PROTOCOL_MODEL_AND_VIEW_NAME, protocol);
		
		
		
		return PROTOCOL_MODEL_AND_VIEW_NAME;
	}
	
	@PostMapping(value = "/searchProtocol")
	String search(@ModelAttribute(PROTOCOL_MODEL_AND_VIEW_NAME) @Valid final ProtocolModel protocolModel, final BindingResult bindingResult) {
		
		return String.format(REDIRECT_PROTOCOL_PATTERN,  protocolModel.getName());
	}
	
	@PostMapping(value = "/showProtocol")
	String showParameter(@ModelAttribute(PROTOCOL_MODEL_AND_VIEW_NAME) final ProtocolModel protocolModel, final Model model) {
	
		System.out.println(protocolModel.getId());
		return PROTOCOL_MODEL_AND_VIEW_NAME;
	}


}
