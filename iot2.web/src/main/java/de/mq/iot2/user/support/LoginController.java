package de.mq.iot2.user.support;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class LoginController {
	
	private final boolean loginRequired;
	
	LoginController(@Value("${iot2.login.required:true}") final boolean loginRequired) {
		this.loginRequired=loginRequired;
	}
	
	@GetMapping(value = "/login")
	String calendar(final Model model) {
		if( !loginRequired) {
			throw new ResourceNotFoundException("/login not found."); 
		}
		return "login";
	}
	

}
