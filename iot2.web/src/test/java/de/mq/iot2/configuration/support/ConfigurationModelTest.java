package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ConfigurationModelTest {
	private final ConfigurationModel configurationModel = new ConfigurationModel();

	@Test
	void id() {
		assertNull(configurationModel.getId());

		final var id = random();
		configurationModel.setId(id);

		assertEquals(id, configurationModel.getId());
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

	@Test
	void parameters() {
		assertTrue(configurationModel.getParameters().isEmpty());

		final Collection<ParameterModel> parameters = List.of(Mockito.mock(ParameterModel.class));
		configurationModel.setParameters(parameters);

		assertEquals(parameters, configurationModel.getParameters());

		configurationModel.setParameters(null);

		assertTrue(configurationModel.getParameters().isEmpty());
	}

	@Test
	void name() {
		assertNull(configurationModel.getName());

		final var name = random();
		configurationModel.setName(name);

		assertEquals(name, configurationModel.getName());
	}

}
