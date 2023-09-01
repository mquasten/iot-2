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
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class SpringConfiguration implements WebMvcConfigurer {

	static final String LOGIN_PAGE = "/login";
	static final String LOCALE_PARAMETER_NANE = "locale";
	static final String ENCODING = "UTF-8";
	static final String I18N_MESSAGE_PATH = "i18n/messages";

	private final boolean loginRequired;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;

	SpringConfiguration(final AuthenticationSuccessHandler authenticationSuccessHandler, @Value("${iot2.login.required:true}") final boolean loginRequired) {
		this.authenticationSuccessHandler = authenticationSuccessHandler;
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

		// localeResolver.setDefaultLocale(Locale.ENGLISH);
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
	public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

		final MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
		http.authorizeHttpRequests((requests) -> translateException(http, mvcMatcherBuilder, requests));
		return http.build();
	}

	private void translateException(HttpSecurity http, final MvcRequestMatcher.Builder mvcMatcherBuilder,
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {
		try {
			filterChain(http, mvcMatcherBuilder, requests);

		} catch (Exception e) {
			throw new IllegalStateException(e);

		}
	}

	private void filterChain(HttpSecurity http, final MvcRequestMatcher.Builder mvcMatcherBuilder,
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) throws Exception {
		if (loginRequired) {
			requests.requestMatchers(mvcMatcherBuilder.pattern("/css/**"), mvcMatcherBuilder.pattern("/css/**"), mvcMatcherBuilder.pattern("/index.html")).permitAll().anyRequest()
					.authenticated().and().formLogin().loginPage(LOGIN_PAGE).successHandler(authenticationSuccessHandler).permitAll().and().logout().permitAll();
		    return;
		} 
			http.authorizeHttpRequests().anyRequest().permitAll();
		
	}

	@Bean()
	@Scope("request")
	SecurityContext securityContext() {
		return SecurityContextHolder.getContext();
	}

}
