package de.mq.iot2.support;

import static de.mq.iot2.support.SpringConfiguration.LOGIN_PAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

public class SpringConfigurationTest {

	private final SpringConfiguration springConfiguration = new SpringConfiguration(true);
	private final static SecurityContext SECURITY_CONTEXT = mock(SecurityContext.class);

	@BeforeAll
	static void setup() {
		SecurityContextHolder.setContext(SECURITY_CONTEXT);
	}

	@Test
	void messageSource() {
		final var messageSource = (ResourceBundleMessageSource) springConfiguration.messageSource();
		assertEquals(1, messageSource.getBasenameSet().size());
		assertEquals(SpringConfiguration.I18N_MESSAGE_PATH, messageSource.getBasenameSet().iterator().next());
		assertEquals(SpringConfiguration.ENCODING, ReflectionTestUtils.getField(messageSource, "defaultEncoding"));
		assertEquals(Locale.GERMAN, ReflectionTestUtils.getField(messageSource, "defaultLocale"));
	}

	@Test
	void addInterceptors() {
		final var interceptorRegistry = Mockito.mock(InterceptorRegistry.class);
		Mockito.doAnswer(this::assertParameterName).when(interceptorRegistry).addInterceptor(Mockito.any());
		springConfiguration.addInterceptors(interceptorRegistry);
	}

	private Object assertParameterName(final InvocationOnMock invocationOnMock) {
		assertEquals(SpringConfiguration.LOCALE_PARAMETER_NANE, invocationOnMock.getArgument(0, LocaleChangeInterceptor.class).getParamName());
		return null;
	}

	@Test
	void localeResolver() {
		final var localeResolver = (springConfiguration.localeResolver());
		assertEquals(Locale.GERMAN, ReflectionTestUtils.getField(localeResolver, "defaultLocale"));
	}

	@Test
	void localeContext() {
		LocaleContextHolder.setLocale(Locale.CHINESE);
		assertEquals(Locale.CHINESE, springConfiguration.localeContext().getLocale());
	}

	@Test
	void daoAuthenticationProvider() {
		final var userDetailsService = mock(UserDetailsService.class);
		final AuthenticationProvider authenticationProvider = springConfiguration.daoAuthenticationProvider(userDetailsService);
		assertEquals(userDetailsService, requiredField(authenticationProvider, UserDetailsService.class));
		assertTrue(requiredField(authenticationProvider, PasswordEncoder.class) instanceof SimpleMessageDigestPasswordEncoderImpl);
	}

	private Object requiredField(final Object authenticationProvider, Class<?> clazz) {
		return DataAccessUtils.requiredUniqueResult(List.of(DaoAuthenticationProvider.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(clazz))
				.map(field -> ReflectionTestUtils.getField(authenticationProvider, field.getName())).collect(Collectors.toList()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	void filterChain() throws Exception {
		final HttpSecurity http = Mockito.mock(HttpSecurity.class);
		final AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry = mock(AuthorizationManagerRequestMatcherRegistry.class);
		final AuthorizeHttpRequestsConfigurer.AuthorizedUrl authorizedUrl = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
		final FormLoginConfigurer<HttpSecurity> formLoginConfigurer = Mockito.mock(FormLoginConfigurer.class);
		final LogoutConfigurer logoutConfigurer = mock(LogoutConfigurer.class);
		when(http.authorizeHttpRequests()).thenReturn(authorizationManagerRequestMatcherRegistry);
		when(authorizationManagerRequestMatcherRegistry.requestMatchers((String[]) Mockito.any())).thenReturn(authorizedUrl);
		when(authorizedUrl.permitAll()).thenReturn(authorizationManagerRequestMatcherRegistry);
		when(authorizationManagerRequestMatcherRegistry.anyRequest()).thenReturn(authorizedUrl);
		when(authorizedUrl.authenticated()).thenReturn(authorizationManagerRequestMatcherRegistry);
		when(authorizationManagerRequestMatcherRegistry.and()).thenReturn(http);
		when(http.formLogin()).thenReturn(formLoginConfigurer);
		when(formLoginConfigurer.loginPage(LOGIN_PAGE)).thenReturn(formLoginConfigurer);
		when(formLoginConfigurer.permitAll()).thenReturn(formLoginConfigurer);
		when(formLoginConfigurer.and()).thenReturn(http);
		when(http.logout()).thenReturn(logoutConfigurer);

		springConfiguration.filterChain(http);

		verify(authorizedUrl).authenticated();
		verify(formLoginConfigurer).loginPage(LOGIN_PAGE);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	void filterChainNoLogin() throws Exception {
		final HttpSecurity http = Mockito.mock(HttpSecurity.class);
		final AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry = mock(AuthorizationManagerRequestMatcherRegistry.class);
		final SpringConfiguration springConfiguration = new SpringConfiguration(false);
		final AuthorizeHttpRequestsConfigurer.AuthorizedUrl authorizedUrl = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
		when(http.authorizeHttpRequests()).thenReturn(authorizationManagerRequestMatcherRegistry);
		when(authorizationManagerRequestMatcherRegistry.anyRequest()).thenReturn(authorizedUrl);

		springConfiguration.filterChain(http);

		verify(authorizationManagerRequestMatcherRegistry).anyRequest();
		verify(authorizedUrl).permitAll();
	}

	@Test
	void securityContext() {
		assertEquals(SECURITY_CONTEXT, springConfiguration.securityContext());
	}

}
