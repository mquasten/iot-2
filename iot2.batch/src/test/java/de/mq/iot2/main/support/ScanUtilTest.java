package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.ReflectionUtils;

class ScanUtilTest {

	@Test
	void findBatchMethods() {
		final var packageName = getClass().getPackage().getName();

		final var results = ScanUtil.findBatchMethods(packageName);

		assertEquals(16, results.size());
		assertTrue(List.of("end-of-day", "setup", "add-local-date", "delete-local-date", "cleanup-calendar","cleanup-protocol", "end-of-day-update", "update-user", "delete-user", "export-calendar",
				"export-configuration", "import-calendar", "import-configuration", "delete-calendar-and-configurations", "delete-configurations" , "export-protocol").containsAll(results.keySet()));
		results.values().forEach(method -> {
			List.of(SetupDatabaseImpl.class, EndOfDayBatchImpl.class).contains(method.getDeclaringClass());
			assertEquals(ReflectionUtils.findMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes()), method);
			assertTrue(method.isAnnotationPresent(BatchMethod.class));
		});
	}

	@Test
	void findBatchMethodsClassCastException() {
		final var packageName = getClass().getPackage().getName();
		final var provider = Mockito.mock(ClassPathScanningCandidateComponentProvider.class);
		BeanDefinition beanDefinition = Mockito.mock(BeanDefinition.class);
		Mockito.when(beanDefinition.getBeanClassName()).thenReturn("invalid");
		Mockito.when(provider.findCandidateComponents(packageName)).thenReturn(Set.of(beanDefinition));

		assertTrue(assertThrows(IllegalStateException.class, () -> ScanUtil.findBatchMethods(packageName, provider)).getCause() instanceof ClassNotFoundException);
	}

}
