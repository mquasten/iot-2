package de.mq.iot2.user.support;

import static de.mq.iot2.user.support.UserController.LOCALES_MODEL;
import static de.mq.iot2.user.support.UserController.USER_MODEL_AND_VIEW_NAME;
import static de.mq.iot2.user.support.UserController.USER_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;

class UserControllerTest {

	private final UserService userService = mock(UserService.class);
	private final SecurityContectRepository securityContectRepository = mock(SecurityContectRepository.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<User, UserModel> userMapper = mock(ModelMapper.class);
	private final SecurityContext securityContext = mock(SecurityContext.class);
	private final Authentication authentication = mock(Authentication.class);
	private final Model model = new ExtendedModelMap();
	private final Collection<String> algorithms = List.of("MD5");
	private final UserController userController = new UserController(userService, securityContectRepository, userMapper);

	@ParameterizedTest
	@MethodSource("locales")
	void login(final Locale locale) {
		final var name = random();
		final User user = new UserImpl(name, random(), Optional.empty());
		when(authentication.getName()).thenReturn(name);
		when(securityContectRepository.securityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(userService.user(name)).thenReturn(Optional.of(user));

		when(userService.algorithms()).thenReturn(algorithms);
		final var userModel = new UserModel();
		when(userMapper.toWeb(user)).thenReturn(userModel);

		assertEquals(USER_MODEL_AND_VIEW_NAME, userController.login(model, locale, true));

		assertEquals(userModel, model.getAttribute(USER_MODEL_AND_VIEW_NAME));
		assertEquals(locale.getLanguage(), userModel.getLocale());
		assertTrue(userModel.isPasswordChanged());

		@SuppressWarnings("unchecked")
		final Map<?, ?> locales = ((Collection<Entry<?, ?>>) model.getAttribute(LOCALES_MODEL)).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		assertEquals(2, locales.size());
		assertTrue(locales.containsKey(Locale.GERMAN.getLanguage()));
		assertTrue(locales.containsKey(Locale.ENGLISH.getLanguage()));
		assertEquals(Locale.GERMAN.getDisplayLanguage(locale), locales.get(Locale.GERMAN.getLanguage()));
		assertEquals(Locale.ENGLISH.getDisplayLanguage(locale), locales.get(Locale.ENGLISH.getLanguage()));

		assertEquals(algorithms, model.getAttribute(UserController.ALGORITHMS_MODEL));

	}

	private static Collection<Locale> locales() {
		return List.of(Locale.GERMAN, Locale.ENGLISH);
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

	@Test
	void loginUserNotFound() {
		final var name = random();
		when(authentication.getName()).thenReturn(name);
		when(securityContectRepository.securityContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(userService.user(name)).thenReturn(Optional.empty());

		assertEquals(String.format(USER_NOT_FOUND_MESSAGE, name),
				assertThrows(EntityNotFoundException.class, () -> userController.login(model, Locale.ENGLISH, true)).getMessage());
	}

}
