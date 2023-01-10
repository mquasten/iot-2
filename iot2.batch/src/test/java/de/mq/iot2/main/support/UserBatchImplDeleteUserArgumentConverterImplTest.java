package de.mq.iot2.main.support;

import static de.mq.iot2.main.support.UserBatchImplDeleteUserArgumentConverterImpl.INVALID_NUMBER_OF_PARAMETERS_MESSAGE_DELETE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class UserBatchImplDeleteUserArgumentConverterImplTest {
	private final String NAME = "jcmaxwell";

	private final Converter<List<String>, Object[]> converter = new UserBatchImplDeleteUserArgumentConverterImpl();

	@Test
	void convert() {
		final Object[] results = converter.convert(List.of(NAME));
		assertEquals(1, results.length);
		assertEquals(NAME, results[0]);
	}

	@Test
	void convertWrongNmberOfArguments() {
		assertEquals(INVALID_NUMBER_OF_PARAMETERS_MESSAGE_DELETE_USER, assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of())).getMessage());
		assertEquals(INVALID_NUMBER_OF_PARAMETERS_MESSAGE_DELETE_USER, assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of(NAME, NAME))).getMessage());
	}

}
