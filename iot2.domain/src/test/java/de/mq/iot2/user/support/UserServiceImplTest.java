package de.mq.iot2.user.support;

import static de.mq.iot2.user.support.UserImpl.PASSWORD_DELIMIER;
import static de.mq.iot2.user.support.UserServiceImpl.USER_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import de.mq.iot2.support.RandomTestUtil;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;

class UserServiceImplTest {

	private final UserRepository userRepository = mock(UserRepository.class);
	private final UserService userService = new UserServiceImpl(userRepository);

	private final String NAME = RandomTestUtil.randomString();
	private final String PASSWORD = RandomTestUtil.randomString();
	private final String ALGORITHM = "MD5";
	private final User user = mock(User.class);

	@Test
	void user() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(user));
		assertEquals(Optional.of(user), userService.user(NAME));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " " })
	@NullSource
	void userNameEmpty(final String name) {
		assertThrows(IllegalArgumentException.class, () -> userService.user(name));

	}

	@Test
	void update() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(user));
		
		userService.update(NAME, PASSWORD, Optional.of("MD5"));
		
		verify(user).assingPassword(PASSWORD, "MD5");
		verify(userRepository).save(user);
	}

	@Test
	void updateWithoutAlgorithm() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(user));
		
		userService.update(NAME, PASSWORD, Optional.empty());
		
		verify(user).assingPassword(PASSWORD);
		verify(userRepository).save(user);
	}

	@Test
	void updateNew() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.empty());
		
		userService.update(NAME, PASSWORD,Optional.of(ALGORITHM));
		
		verify(userRepository).save(argThat(user -> user.name().equals(NAME) && user.encodedPasswordAndAlgorithm().equals(String.format("%s%s%s", new DigestUtils(ALGORITHM).digestAsHex(PASSWORD.getBytes()), PASSWORD_DELIMIER, ALGORITHM))));
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " " })
	@NullSource
	void updateNulls(final String value) {
		assertThrows(IllegalArgumentException.class, () -> userService.update(value, PASSWORD, Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> userService.update(NAME, value, Optional.empty()));
	}

	@Test
	void updateNulls() {
		assertThrows(IllegalArgumentException.class, () -> userService.update(NAME, PASSWORD, null));
	}

	@Test
	void updateLanguage() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(user));
		
		userService.update(NAME, Locale.GERMAN);
		
		verify(user).assignLanguage(Locale.GERMAN);
		verify(userRepository).save(user);
	}

	@Test
	void updateLanguageNulls() {
		assertThrows(IllegalArgumentException.class, () -> userService.update(null, Locale.GERMAN));
		assertThrows(IllegalArgumentException.class, () -> userService.update(NAME, null));
	}

	@Test
	void updateLanguageUserNotFound() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.empty());
		assertEquals(String.format(USER_NOT_FOUND_MESSAGE, NAME),assertThrows(EntityNotFoundException.class, () -> userService.update(NAME, Locale.GERMAN)).getMessage());
	}

	@Test
	void delete() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(user));
		
		assertTrue(userService.delete(NAME));
		
		verify(userRepository).delete(user);
	}

	@Test
	void deleteUserNotFound() {
		Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.empty());
		
		assertFalse(userService.delete(NAME));
		
		verify(userRepository, never()).delete(user);
	}

	@Test
	void algorithms() {
		final Collection<String> results = userService.algorithms();

		assertEquals(15, results.size());
		assertTrue(results.contains("MD5"));
	}
}
