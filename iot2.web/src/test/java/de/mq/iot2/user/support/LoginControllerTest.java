package de.mq.iot2.user.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

class LoginControllerTest {
	@Test
	void login() {
		assertEquals(LoginController.LOGIN_VIEW, new LoginController(true).login());
	}

	@Test
	void loginNoLoginRequired() {
		assertThrows(ResourceNotFoundException.class, () -> new LoginController(false).login());
	}

}
