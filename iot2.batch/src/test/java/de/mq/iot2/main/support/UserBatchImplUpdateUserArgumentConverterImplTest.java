package de.mq.iot2.main.support;

import static de.mq.iot2.main.support.UserBatchImplUpdateUserArgumentConverterImpl.INVALID_NUMBER_OF_PARAMETERS_MESSAGE_UPDATE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class UserBatchImplUpdateUserArgumentConverterImplTest {
	private final Converter<List<String>, Object[]> converter = new UserBatchImplUpdateUserArgumentConverterImpl();

	private final String NAME = "jcmaxwell";

	private final String PASSWORD = "rotE=-dB/dt";

	private final String ALGORITHM = "MD5";

	@Test
	void convert() {
		final Object[] results = converter.convert(List.of(NAME, PASSWORD, ALGORITHM));
		assertEquals(3, results.length);
		assertEquals(NAME, results[0]);
		assertEquals(PASSWORD, results[1]);
		assertEquals(ALGORITHM, results[2]);
	}

	@Test
	void convertNoAlgorithm() {
		final Object[] results = converter.convert(List.of(NAME, PASSWORD));
		assertEquals(3, results.length);
		assertEquals(NAME, results[0]);
		assertEquals(PASSWORD, results[1]);
		assertNull(results[2]);
	}

	@Test
	void convertWrongNmberOfArguments() {
		assertEquals(INVALID_NUMBER_OF_PARAMETERS_MESSAGE_UPDATE_USER, assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of(NAME))).getMessage());
		assertEquals(INVALID_NUMBER_OF_PARAMETERS_MESSAGE_UPDATE_USER,
				assertThrows(IllegalArgumentException.class, () -> converter.convert(List.of(NAME, PASSWORD, ALGORITHM, NAME))).getMessage());
	}

}
