package de.mq.iot2.support;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class SpringConfiguration implements WebMvcConfigurer {

	static final String LOGIN_PAGE = "/login";
	static final String LOCALE_PARAMETER_NANE = "locale";
	static final String ENCODING = "UTF-8";
	static final String I18N_MESSAGE_PATH = "i18n/messages";

	private final boolean loginRequired;

	SpringConfiguration(@Value("${iot2.login.required:true}") final boolean loginRequired) {
		this.loginRequired = loginRequired;
	}

	@Bean(name = "messageSource")
	MessageSource messageSource() {
		final var messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename(I18N_MESSAGE_PATH);
		messageSource.setDefaultEncoding(ENCODING);
		messageSource.setDefaultLocale(Locale.GERMAN);

		return messageSource;
	}

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		final LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName(LOCALE_PARAMETER_NANE);
		registry.addInterceptor(interceptor);
	}

	@Bean
	LocaleResolver localeResolver() {
		final var localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.GERMAN);
		return localeResolver;
	}

	@Bean()
	@Scope("request")
	LocaleContext localeContext() {
		return LocaleContextHolder.getLocaleContext();
	}

	@Bean
	AuthenticationProvider daoAuthenticationProvider(final UserDetailsService userDetailsService) {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(new SimpleMessageDigestPasswordEncoderImpl());
		authenticationProvider.setUserDetailsService(userDetailsService);
		return authenticationProvider;
	}

	@Bean
	SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

		if (loginRequired) {
			http.authorizeHttpRequests().requestMatchers("/css/**", "/error", "/", "/index.html").permitAll().anyRequest().authenticated().and().formLogin().loginPage(LOGIN_PAGE)
					.permitAll().and().logout().permitAll();
		} else {
			http.authorizeHttpRequests().anyRequest().permitAll();
		}
		return http.build();
	}

}
