package de.mq.iot2.user.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LoginSuccessHandlerImplTest {
	
	
	private final UserService userService = mock(UserService.class);
	private final AuthenticationSuccessHandler  authenticationSuccessHandler = new LoginSuccessHandlerImpl(userService);
	private final Authentication authentication = Mockito.mock(Authentication.class);
	private final HttpServletRequest servletRequest = new MockHttpServletRequest();
	private final HttpServletResponse servletResponse = new MockHttpServletResponse();
	
	@Test
	void onAuthenticationSuccess() throws IOException, ServletException {
		final var locale = Locale.GERMAN;
		final User user = mock(User.class);
		when(user.language()).thenReturn(Optional.of(locale));
		final String name  = UUID.randomUUID().toString();
		when(authentication.getName()).thenReturn(name);
		when(userService.user(name)).thenReturn(Optional.of(user));
		
		authenticationSuccessHandler.onAuthenticationSuccess(servletRequest, servletResponse, authentication);
		assertEquals(locale, servletRequest.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME));
	}
	
	@Test
	void onAuthenticationSuccessUserNotFound() throws IOException, ServletException {
		authenticationSuccessHandler.onAuthenticationSuccess(servletRequest, servletResponse, authentication);
		assertNull(servletRequest.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME));
	}
	
	@Test
	void onAuthenticationSuccessNoLanguageStored() throws IOException, ServletException {
		final User user = mock(User.class);
		when(user.language()).thenReturn(Optional.empty());
		final String name  = UUID.randomUUID().toString();
		when(authentication.getName()).thenReturn(name);
		when(userService.user(name)).thenReturn(Optional.of(user));
		
		authenticationSuccessHandler.onAuthenticationSuccess(servletRequest, servletResponse, authentication);
		assertNull(servletRequest.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME));
	}

}
