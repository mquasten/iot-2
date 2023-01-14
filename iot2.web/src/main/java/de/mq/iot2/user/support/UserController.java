package de.mq.iot2.user.support;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.security.Security;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
class UserController{
	private static final String REDIRECT_ROOT = "redirect:/";
	private static final String  USER_MODEL_AND_VIEW_NAME ="user";
	private static final String  USER_MODEL_AND_VIEW_NAME_REDIRECT_CHANGE ="redirect:"+USER_MODEL_AND_VIEW_NAME+"?changed=true";
	private static final String  USER_MODEL_AND_VIEW_NAME_REDIRECT_LOCALE_PATTERN ="redirect:"+USER_MODEL_AND_VIEW_NAME+"?locale=%s";
	private final SecurityContectRepository securityContectRepository;
	private final UserService userService;
	private final  ModelMapper<User, UserModel> userMapper;
	private final static String MESSAGE_KEY_PASSWORDS_DIFFERENT="error.passwords.different";
	
	UserController(final UserService userService, final SecurityContectRepository securityContectRepository, final  ModelMapper<User, UserModel> userMapper) {
		this.userService=userService;
		this.securityContectRepository=securityContectRepository;
		this.userMapper=userMapper;
	}
	@GetMapping(value = "/user")
	String login(final Model model , final Locale locale, @RequestParam(name = "changed", required = false ) final boolean changed){
		final String name = securityContectRepository.securityContext().getAuthentication().getName();
		Assert.hasText(name, "Name is required.");
		final UserModel userModel= userMapper.toWeb(userService.user(name).orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found.", name))));
		userModel.setLocale(locale.getLanguage());
		userModel.setPasswordChanged(changed);
		model.addAttribute(USER_MODEL_AND_VIEW_NAME, userModel);
		
		model.addAttribute("locales", List.of(new SimpleImmutableEntry<>(Locale.GERMAN.getLanguage(), Locale.GERMAN.getDisplayLanguage(locale)), new SimpleImmutableEntry<>(Locale.ENGLISH.getLanguage(), Locale.ENGLISH.getDisplayLanguage(locale))));
		addMessageDigests(model);
		return USER_MODEL_AND_VIEW_NAME;
	}
	private void addMessageDigests(final Model model) {
		model.addAttribute("algorithms", Security.getAlgorithms("MessageDigest").stream().sorted().collect(Collectors.toList()));
	}
	
	@PostMapping(value = "/changePassword")
	String changePassword(@ModelAttribute(USER_MODEL_AND_VIEW_NAME) @Valid final UserModel user,final BindingResult bindingResult, final Model model) {
		if(bindingResult.hasErrors()) {
			addMessageDigests(model);
			return USER_MODEL_AND_VIEW_NAME;
		}
		
		if( ! ObjectUtils.defaultIfNull(user.getPassword(), EMPTY).equals(ObjectUtils.defaultIfNull(user.getConfirmedPassword(), EMPTY))) {
			bindingResult.addError(new ObjectError(USER_MODEL_AND_VIEW_NAME, new String[] { MESSAGE_KEY_PASSWORDS_DIFFERENT }, null, MESSAGE_KEY_PASSWORDS_DIFFERENT));
			return USER_MODEL_AND_VIEW_NAME;
		}
		
		userService.update(user.getName(), user.getPassword(), StringUtils.hasText(user.getAlgorithm())? Optional.of(user.getAlgorithm()): Optional.empty());
	
		return USER_MODEL_AND_VIEW_NAME_REDIRECT_CHANGE;
	}

	
	@PostMapping(value = "/changeLanguage")
	String changeLanguage(@ModelAttribute(USER_MODEL_AND_VIEW_NAME) final UserModel user) {
		return String.format(USER_MODEL_AND_VIEW_NAME_REDIRECT_LOCALE_PATTERN, user.getLocale());
	}
	
	@PostMapping(value = "/userLogout")
	String logout(final HttpServletRequest request) {
		securityContectRepository.securityContext().setAuthentication(null);
		request.getSession().invalidate();
		return REDIRECT_ROOT;
	}
	

}
