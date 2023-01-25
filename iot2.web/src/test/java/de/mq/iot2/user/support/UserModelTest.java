package de.mq.iot2.user.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserModelTest {

	private final UserModel userModel = new UserModel();

	@Test
	void name() {
		assertNull(userModel.getName());
		final var name = random();
		userModel.setName(name);

		assertEquals(name, userModel.getName());
	}

	@Test
	void algorithm() {
		assertNull(userModel.getAlgorithm());
		final var algorithm = random();
		userModel.setAlgorithm(algorithm);

		assertEquals(algorithm, userModel.getAlgorithm());
	}

	@Test
	void locale() {
		assertNull(userModel.getLocale());
		final var locale = random();
		userModel.setLocale(locale);

		assertEquals(locale, userModel.getLocale());
	}

	@Test
	void password() {
		assertNull(userModel.getPassword());
		final var password = random();
		userModel.setPassword(password);

		assertEquals(password, userModel.getPassword());
	}

	@Test
	void confirmedPassword() {
		assertNull(userModel.getConfirmedPassword());
		final var confirmedPassword = random();
		userModel.setConfirmedPassword(confirmedPassword);

		assertEquals(confirmedPassword, userModel.getConfirmedPassword());
	}

	@Test
	void passwordChanged() {
		assertFalse(userModel.isPasswordChanged());
		userModel.setPasswordChanged(true);

		assertTrue(userModel.isPasswordChanged());
	}

	@Test
	void isLoginRequired() {
		assertFalse(userModel.isLoginRequired());
		userModel.setLoginRequired(true);

		assertTrue(userModel.isLoginRequired());
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

}
