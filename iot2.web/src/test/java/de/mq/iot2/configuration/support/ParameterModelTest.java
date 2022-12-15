package de.mq.iot2.configuration.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ParameterModelTest {

	private final ParameterModel parameterModel = new ParameterModel();

	@Test
	void id() {
		assertNull(parameterModel.getId());

		final var id = random();
		parameterModel.setId(id);

		assertEquals(id, parameterModel.getId());
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

	@Test
	void name() {
		assertNull(parameterModel.getName());

		final var name = random();
		parameterModel.setName(name);

		assertEquals(name, parameterModel.getName());
	}

	@Test
	void value() {
		assertNull(parameterModel.getValue());

		final var value = random();
		parameterModel.setValue(value);

		assertEquals(value, parameterModel.getValue());
	}

	@Test
	void configuration() {
		assertNull(parameterModel.getConfiguration());

		final var configuration = random();
		parameterModel.setConfiguration(configuration);

		assertEquals(configuration, parameterModel.getConfiguration());
	}

	@Test
	void configurationId() {
		assertNull(parameterModel.getConfigurationId());

		final var configurationId = random();
		parameterModel.setConfigurationId(configurationId);

		assertEquals(configurationId, parameterModel.getConfigurationId());
	}

	@Test
	void cycle() {
		assertNull(parameterModel.getCycle());

		final var cycle = random();
		parameterModel.setCycle(cycle);

		assertEquals(cycle, parameterModel.getCycle());
	}

	@Test
	void cycleId() {
		assertNull(parameterModel.getCycleId());

		final var cycleId = random();
		parameterModel.setCycleId(cycleId);

		assertEquals(cycleId, parameterModel.getCycleId());
	}

}
