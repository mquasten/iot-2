package de.mq.iot2.configuration.support;

import static de.mq.iot2.configuration.support.ParameterMapper.PARAMETER_NOT_FOUND_MESSAGE;
import static de.mq.iot2.support.IdUtil.assignId;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleImpl;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.EntityNotFoundException;

class ParameterMapperTest {

	private static final String PARAMETER_ID = randomUUID().toString();
	private static final String CYCLE_PARAMETER_ID = randomUUID().toString();
	private static final String CONFIGURATION_ID = randomUUID().toString();
	private static final String CYCLE_ID = randomUUID().toString();
	private final ParameterRepository parameterRepository = mock(ParameterRepository.class);
	private final ParameterMapper parameterMapper = new ParameterMapper(parameterRepository, new ConfigurationBeans().conversionService());
	private final Configuration configuration = new ConfigurationImpl(RuleKey.EndOfDay, "Configuration-Name");
	private final Parameter parameter = new ParameterImpl(configuration, Key.UpTime, "05:30");
	private final Cycle cycle = BeanUtils.instantiateClass(findConstructor(), "Cycle-Name", 0);
	private final Parameter cycleParameter = new CycleParameterImpl(configuration, Key.UpTime, "05:30", cycle);

	private Constructor<? extends Cycle> findConstructor() {
		try {
			return CycleImpl.class.getDeclaredConstructor(String.class, int.class);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	@BeforeEach
	void setUp() {
		assignId(configuration, CONFIGURATION_ID);
		assignId(parameter, PARAMETER_ID);
		assignId(cycleParameter, CYCLE_PARAMETER_ID);
		assignId(cycle, CYCLE_ID);
	}

	@Test
	void toWeb() {
		final var parameterModel = parameterMapper.toWeb(parameter);

		assertEquals(PARAMETER_ID, parameterModel.getId());
		assertEquals(CONFIGURATION_ID, parameterModel.getConfigurationId());
		assertEquals(configuration.name(), parameterModel.getConfiguration());
		assertEquals(parameter.key().name(), parameterModel.getName());
		assertEquals(parameter.value(), parameterModel.getValue());
		assertNull(parameterModel.getCycleId());
		assertNull(parameterModel.getCycle());
	}

	@Test
	void toWebCycleParameter() {
		final var parameterModel = parameterMapper.toWeb(cycleParameter);

		assertEquals(CYCLE_PARAMETER_ID, parameterModel.getId());
		assertEquals(CONFIGURATION_ID, parameterModel.getConfigurationId());
		assertEquals(configuration.name(), parameterModel.getConfiguration());
		assertEquals(parameter.key().name(), parameterModel.getName());
		assertEquals(parameter.value(), parameterModel.getValue());
		assertEquals(CYCLE_ID, parameterModel.getCycleId());
		assertEquals(cycle.name(), parameterModel.getCycle());
	}

	@Test
	void toDomainById() {
		Mockito.when(parameterRepository.findById(PARAMETER_ID)).thenReturn(Optional.of(parameter));

		assertEquals(parameter, parameterMapper.toDomain(PARAMETER_ID));
	}

	@Test
	void toDomainNotFound() {
		assertEquals(String.format(PARAMETER_NOT_FOUND_MESSAGE, PARAMETER_ID),
				assertThrows(EntityNotFoundException.class, () -> parameterMapper.toDomain(PARAMETER_ID)).getMessage());
	}

	@Test
	void toDomain() {
		final var parameterModel = newParamterModel(PARAMETER_ID);
		when(parameterRepository.findById(PARAMETER_ID)).thenReturn(Optional.of(parameter));

		final var result = parameterMapper.toDomain(parameterModel);

		assertEquals(PARAMETER_ID, IdUtil.getId(result));
		assertEquals(Key.UpTime, result.key());
		assertEquals(configuration, result.configuration());
		assertEquals("07:15", result.value());
		assertTrue(result instanceof ParameterImpl);
	}

	private ParameterModel newParamterModel(final String id) {
		final ParameterModel parameterModel = new ParameterModel();
		parameterModel.setId(id);
		parameterModel.setConfiguration(configuration.name());
		parameterModel.setConfigurationId(CONFIGURATION_ID);
		parameterModel.setName(parameter.key().name());
		parameterModel.setValue("7:15");
		return parameterModel;
	}

	@Test
	void toDomainCyleParameter() {
		final var parameterModel = newParamterModel(CYCLE_PARAMETER_ID);
		parameterModel.setConfigurationId(CONFIGURATION_ID);
		when(parameterRepository.findById(CYCLE_PARAMETER_ID)).thenReturn(Optional.of(cycleParameter));

		final var result = (CycleParameter) parameterMapper.toDomain(parameterModel);

		assertEquals(CYCLE_PARAMETER_ID, IdUtil.getId(result));
		assertEquals(Key.UpTime, result.key());
		assertEquals(configuration, result.configuration());
		assertEquals("07:15", result.value());
		assertEquals(cycle, result.cycle());
		assertTrue(result instanceof CycleParameterImpl);
	}
}
