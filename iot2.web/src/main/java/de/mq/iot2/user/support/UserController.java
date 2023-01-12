package de.mq.iot2.user.support;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;

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
		userModel.setLocale(locale);
		model.addAttribute(USER_MODEL_AND_VIEW_NAME, userModel);
		return USER_MODEL_AND_VIEW_NAME;
	}

	
	
	

}
