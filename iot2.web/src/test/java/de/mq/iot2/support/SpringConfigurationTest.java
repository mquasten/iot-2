package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

public class SpringConfigurationTest {
	private final AuthenticationSuccessHandler authenticationSuccessHandler = mock(AuthenticationSuccessHandler.class);
	private final SpringConfiguration springConfiguration = new SpringConfiguration(authenticationSuccessHandler, true);
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
		final var localeResolver = springConfiguration.localeResolver();
		assertNull(ReflectionTestUtils.getField(localeResolver, "defaultLocale"));
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
		assertTrue(((Supplier<?>) requiredField(authenticationProvider, Supplier.class)).get() instanceof SimpleMessageDigestPasswordEncoderImpl);
	}

	private Object requiredField(final Object authenticationProvider, Class<?> clazz) {
		return DataAccessUtils.requiredUniqueResult(
				List.of(DaoAuthenticationProvider.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(clazz)).map(field -> ReflectionTestUtils.getField(authenticationProvider, field.getName())).collect(Collectors.toList()));
	}

	@Test
	void securityContext() {
		assertEquals(SECURITY_CONTEXT, springConfiguration.securityContext());
	}

}
