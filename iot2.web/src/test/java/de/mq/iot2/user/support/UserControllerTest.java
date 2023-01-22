package de.mq.iot2.user.support;

import static de.mq.iot2.user.support.UserController.LOCALES_MODEL;
import static de.mq.iot2.user.support.UserController.MESSAGE_KEY_PASSWORDS_DIFFERENT;
import static de.mq.iot2.user.support.UserController.USER_MODEL_AND_VIEW_NAME;
import static de.mq.iot2.user.support.UserController.USER_MODEL_AND_VIEW_NAME_REDIRECT_CHANGE;
import static de.mq.iot2.user.support.UserController.USER_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

class UserControllerTest {

	private static final String ALGORITHM = "MD5";
	private static final String NAME = random();
	private static final String PASSWORD = random();
	private final UserService userService = mock(UserService.class);
	private final SecurityContectRepository securityContectRepository = mock(SecurityContectRepository.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<User, UserModel> userMapper = mock(ModelMapper.class);
	private final SecurityContext securityContext = mock(SecurityContext.class);
	private final Authentication authentication = mock(Authentication.class);
	private final Model model = new ExtendedModelMap();
	private final Collection<String> algorithms = List.of(ALGORITHM);
	private final UserController userController = new UserController(userService, securityContectRepository, userMapper, true);
	private final UserModel userModel = new UserModel();
	private final BindingResult bindingResult = mock(BindingResult.class);

	@ParameterizedTest
	@MethodSource("locales")
	void login(final Locale locale) {
		final User user = new UserImpl(NAME, random(), Optional.empty());
		when(authentication.getName()).thenReturn(NAME);
		when(securityContectRepository.securityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(userService.user(NAME)).thenReturn(Optional.of(user));
		when(userService.algorithms()).thenReturn(algorithms);
		when(userMapper.toWeb(user)).thenReturn(userModel);

		assertEquals(USER_MODEL_AND_VIEW_NAME, userController.login(model, locale, true));

		assertEquals(userModel, model.getAttribute(USER_MODEL_AND_VIEW_NAME));
		assertEquals(locale.getLanguage(), userModel.getLocale());
		assertTrue(userModel.isPasswordChanged());
		assertTrue(userModel.isLoginRequired());

		@SuppressWarnings("unchecked")
		final Map<?, ?> locales = ((Collection<Entry<?, ?>>) model.getAttribute(LOCALES_MODEL)).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(2, locales.size());
		assertTrue(locales.containsKey(Locale.GERMAN.getLanguage()));
		assertTrue(locales.containsKey(Locale.ENGLISH.getLanguage()));
		assertEquals(Locale.GERMAN.getDisplayLanguage(locale), locales.get(Locale.GERMAN.getLanguage()));
		assertEquals(Locale.ENGLISH.getDisplayLanguage(locale), locales.get(Locale.ENGLISH.getLanguage()));
		assertEquals(algorithms, model.getAttribute(UserController.ALGORITHMS_MODEL));
		
	}
	
	
	@ParameterizedTest
	@MethodSource("locales")
	void loginLoginNotRequired(final Locale locale) {
		UserController userController = new UserController(userService, securityContectRepository, userMapper, false);
		when(authentication.getName()).thenReturn(NAME);
		when(securityContectRepository.securityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(userService.algorithms()).thenReturn(algorithms);	

		assertEquals(USER_MODEL_AND_VIEW_NAME, userController.login(model, locale, true));

		final UserModel userModel = (UserModel) model.getAttribute(USER_MODEL_AND_VIEW_NAME);
		assertEquals(locale.getLanguage(), userModel.getLocale());
		assertFalse(userModel.isLoginRequired());
		assertTrue(userModel.isPasswordChanged());
		assertFalse(userModel.isLoginRequired());

		@SuppressWarnings("unchecked")
		final Map<?, ?> locales = ((Collection<Entry<?, ?>>) model.getAttribute(LOCALES_MODEL)).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(2, locales.size());
		assertTrue(locales.containsKey(Locale.GERMAN.getLanguage()));
		assertTrue(locales.containsKey(Locale.ENGLISH.getLanguage()));
		assertEquals(Locale.GERMAN.getDisplayLanguage(locale), locales.get(Locale.GERMAN.getLanguage()));
		assertEquals(Locale.ENGLISH.getDisplayLanguage(locale), locales.get(Locale.ENGLISH.getLanguage()));

		assertNull(model.getAttribute(UserController.ALGORITHMS_MODEL));
		
		verify(userService, never()).user(any());
		verify(userMapper, never()).toWeb(Mockito.any(User.class));
	}

	private static Collection<Locale> locales() {
		return List.of(Locale.GERMAN, Locale.ENGLISH);
	}

	private static String random() {
		return UUID.randomUUID().toString();
	}

	@Test
	void loginUserNotFound() {
		final var name = random();
		when(authentication.getName()).thenReturn(name);
		when(securityContectRepository.securityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(userService.user(name)).thenReturn(Optional.empty());

		assertEquals(String.format(USER_NOT_FOUND_MESSAGE, name),assertThrows(EntityNotFoundException.class, () -> userController.login(model, Locale.ENGLISH, true)).getMessage());
	}

	@Test
	void changePassword() {
		userModel.setPassword(PASSWORD);
		userModel.setConfirmedPassword(PASSWORD);
		userModel.setAlgorithm(ALGORITHM);
		userModel.setName(NAME);

		assertEquals(USER_MODEL_AND_VIEW_NAME_REDIRECT_CHANGE, userController.changePassword(userModel, bindingResult, model));

		Mockito.verify(userService).update(NAME, PASSWORD, Optional.of(ALGORITHM));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { "", " " })
	void changePasswordWithoutAlogrithm(final String algorithm) {
		userModel.setPassword(PASSWORD);
		userModel.setConfirmedPassword(PASSWORD);
		userModel.setAlgorithm(algorithm);
		userModel.setName(NAME);

		assertEquals(USER_MODEL_AND_VIEW_NAME_REDIRECT_CHANGE, userController.changePassword(userModel, bindingResult, model));

		Mockito.verify(userService).update(NAME, PASSWORD, Optional.empty());
	}

	@Test
	void changePasswordValidationError() {
		
		when(bindingResult.hasErrors()).thenReturn(true);
		
		assertEquals(USER_MODEL_AND_VIEW_NAME, userController.changePassword(userModel, bindingResult, model));
		
		verify(userService, Mockito.never()).update(NAME,PASSWORD, Optional.of(ALGORITHM) );
	}

	@Test
	void changePasswordPasswordsDifferent() {
		userModel.setPassword(PASSWORD);
		userModel.setConfirmedPassword(random());
		userModel.setAlgorithm(ALGORITHM);
		userModel.setName(NAME);

		assertEquals(USER_MODEL_AND_VIEW_NAME, userController.changePassword(userModel, bindingResult, model));

		verify(userService, Mockito.never()).update(NAME, PASSWORD, Optional.of(ALGORITHM));
		verify(bindingResult).addError(argThat(objectError -> objectError.getObjectName().equals(USER_MODEL_AND_VIEW_NAME) && objectError.getCodes().length == 1
				&& objectError.getCodes()[0].equals(MESSAGE_KEY_PASSWORDS_DIFFERENT) && objectError.getDefaultMessage().equals(MESSAGE_KEY_PASSWORDS_DIFFERENT)));
	}
	
	@ParameterizedTest
	@MethodSource("locales")
	void changeLanguage(Locale locale) {
		userModel.setLocale(locale.getLanguage());
		
		assertEquals(String.format(UserController.USER_MODEL_AND_VIEW_NAME_REDIRECT_LOCALE_PATTERN,  locale.getLanguage()), userController.changeLanguage(userModel));
	}
	
	@Test
	void logout() {
		when(securityContectRepository.securityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		final HttpServletRequest request= mock(HttpServletRequest.class);
		final HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		
		assertEquals(UserController.REDIRECT_ROOT, userController.logout(request));
		
		verify(session).invalidate();
		verify(securityContext).setAuthentication(null);
	}

}
