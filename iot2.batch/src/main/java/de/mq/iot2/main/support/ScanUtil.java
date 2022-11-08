package de.mq.iot2.main.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

public interface ScanUtil {

	static Map<String, Method> findBatchMethods(final String packageName) {
		return findBatchMethods(packageName, providerForServiceAnnotation());
	}

	private static ClassPathScanningCandidateComponentProvider providerForServiceAnnotation() {
		final var provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Service.class));
		return provider;
	}

	static Map<String, Method> findBatchMethods(final String packageName,
			final ClassPathScanningCandidateComponentProvider provider) {

		final Map<String, Method> methods = new HashMap<>();
		provider.findCandidateComponents(packageName).forEach(bd -> addIfAnnotated(bd, methods));

		return methods;

	}

	private static void addIfAnnotated(final BeanDefinition beanDefinition, final Map<String, Method> methods) {
		try {
			final Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
			for (final Method method : Arrays.asList(clazz.getDeclaredMethods())) {

				if (method.isAnnotationPresent(BatchMethod.class)) {
					methods.put(method.getDeclaredAnnotation(BatchMethod.class).value(), method);
				}
			}

		} catch (final ClassNotFoundException ex) {
			throw new IllegalStateException(ex);
		}

	}

}
