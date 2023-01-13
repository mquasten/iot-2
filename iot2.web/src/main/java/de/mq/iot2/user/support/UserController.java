package de.mq.iot2.user.support;

import java.security.Security;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
class UserController{
	private static final String  USER_MODEL_AND_VIEW_NAME ="user";
	private final SecurityContectRepository securityContectRepository;
	private final UserService userService;
	private final  ModelMapper<User, UserModel> userMapper;
	
	UserController(final UserService userService, final SecurityContectRepository securityContectRepository, final  ModelMapper<User, UserModel> userMapper) {
		this.userService=userService;
		this.securityContectRepository=securityContectRepository;
		this.userMapper=userMapper;
	}
	@GetMapping(value = "/user")
	String login(final Model model , final Locale locale ) {
		final String name = securityContectRepository.securityContext().getAuthentication().getName();
		Assert.hasText(name, "Name is required.");
		final UserModel userModel= userMapper.toWeb(userService.user(name).orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found.", name))));
		userModel.setLocale(locale.getCountry());
		model.addAttribute(USER_MODEL_AND_VIEW_NAME, userModel);
		
		addMessageDigests(model);
		return USER_MODEL_AND_VIEW_NAME;
	}
	private void addMessageDigests(final Model model) {
		model.addAttribute("algorithms", Security.getAlgorithms("MessageDigest").stream().sorted().collect(Collectors.toList()));
	}
	
	@PostMapping(value = "/changePassword")
	String changePassword(@ModelAttribute(USER_MODEL_AND_VIEW_NAME) @Valid final UserModel user,final BindingResult bindingResult, final Model model) {
		System.out.println(user.getId());
		System.out.println(user.getName());
		System.out.println(user.getPassword());
		System.out.println(user.getAlgorithm());
		
		if(bindingResult.hasErrors()) {
			System.out.println("errrors");
			return USER_MODEL_AND_VIEW_NAME;
		}
		
		return "redirect:"+USER_MODEL_AND_VIEW_NAME;
	}

	
	
	

}
