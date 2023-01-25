package de.mq.iot2.user.support;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import de.mq.iot2.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
class LoginSuccessHandlerImpl extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private final UserService userService;

	LoginSuccessHandlerImpl(final UserService userService) {
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws ServletException, IOException {
		userService.user(authentication.getName()).ifPresent(user -> user.language().ifPresent(language ->  request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME , language)));
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
