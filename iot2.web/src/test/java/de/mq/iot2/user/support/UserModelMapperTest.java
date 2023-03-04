package de.mq.iot2.user.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;

class UserModelMapperTest {

	@Test
	void toWeb() {
		final ModelMapper<User, UserModel> mapper = new UserModelMapper();
		final var password = random();
		final User user = new UserImpl(password, password, Optional.of("MD5"));

		final UserModel userModel = mapper.toWeb(user);

		assertEquals(user.name(), userModel.getName());
		assertFalse(userModel.isLoginRequired());
		assertEquals(DigestUtils.md5DigestAsHex(password.getBytes()), userModel.getPassword());
		assertEquals(user.algorithm().get(), userModel.getAlgorithm());
		assertFalse(userModel.isPasswordChanged());
		assertNull(userModel.getConfirmedPassword());
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

}
