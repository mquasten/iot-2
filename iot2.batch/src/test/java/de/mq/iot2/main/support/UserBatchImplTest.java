package de.mq.iot2.main.support;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import de.mq.iot2.user.UserService;

class UserBatchImplTest {

	private final UserService userService = mock(UserService.class);

	private final UserBatchImpl userBatch = new UserBatchImpl(userService);

	private final String NAME = "jcmaxwell";

	private final String PASSWORD = "rotE=-dB/dt";

	private final String ALGORITHM = "MD5";

	@Test
	void updateUser() {
		userBatch.updateUser(NAME, PASSWORD, ALGORITHM);
		verify(userService).update(NAME, PASSWORD, Optional.of(ALGORITHM));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { " ", "" })
	void updateUserNoPasswordCryption(final String value) {
		userBatch.updateUser(NAME, PASSWORD, value);
		verify(userService).update(NAME, PASSWORD, Optional.empty());
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void deleteUser(final boolean exists) {
		Mockito.when(userService.delete(NAME)).thenReturn(exists);
		userBatch.deleteUser(NAME);
		verify(userService).delete(NAME);
	}

}
