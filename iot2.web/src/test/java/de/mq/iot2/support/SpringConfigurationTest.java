package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

public class SpringConfigurationTest {

	private final SpringConfiguration springConfiguration = new SpringConfiguration();

	
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
		assertEquals(SpringConfiguration.LOCALE_PARAMETER_NANE,
				invocationOnMock.getArgument(0, LocaleChangeInterceptor.class).getParamName());
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

}
