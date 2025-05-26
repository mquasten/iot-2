package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.configuration.Parameter;
import de.mq.iot2.configuration.support.ParameterModel;

class ModelMapperTest {
	private final List<Parameter> domainList = List.of(Mockito.mock(Parameter.class), Mockito.mock(Parameter.class));

	private Map<Parameter, ParameterModel> resultMap = Map.of(domainList.get(0), new ParameterModel(), domainList.get(1), new ParameterModel());

	@SuppressWarnings("unchecked")
	private final ModelMapper<Parameter, ParameterModel> modelMapper = Mockito.spy(ModelMapper.class);

	@Test
	void toWeb() {
		Mockito.when(modelMapper.toWeb(Mockito.anyCollection())).thenCallRealMethod();
		Mockito.doAnswer(answer -> resultMap.get(answer.getArgument(0, Parameter.class))).when(modelMapper).toWeb(Mockito.any(Parameter.class));

		final List<ParameterModel> results = List.copyOf(modelMapper.toWeb(domainList));

		IntStream.range(0, 2).forEach(i -> assertEquals(resultMap.get(domainList.get(i)), results.get(i)));

	}

	@Test
	void toWebById() {
		final var id = UUID.randomUUID().toString();
		Mockito.doAnswer(_ -> domainList.get(0)).when(modelMapper).toDomain(id);
		Mockito.when(modelMapper.toWeb(domainList.get(0))).thenReturn(resultMap.get(domainList.get(0)));
		Mockito.when(modelMapper.toWeb(id)).thenCallRealMethod();

		assertEquals(resultMap.get(domainList.get(0)), modelMapper.toWeb(id));
	}

	@Test
	void toDomain() {
		assertEquals(ModelMapper.METHOD_NOT_IMPLEMENTED,
				assertThrows(UnsupportedOperationException.class, () -> modelMapper.toDomain(resultMap.get(domainList.get(0)))).getMessage());
	}

	@Test
	void toDomainById() {
		assertEquals(ModelMapper.METHOD_NOT_IMPLEMENTED, assertThrows(UnsupportedOperationException.class, () -> modelMapper.toDomain(UUID.randomUUID().toString())).getMessage());
	}

}
