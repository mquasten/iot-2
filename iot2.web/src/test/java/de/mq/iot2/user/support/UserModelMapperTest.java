package de.mq.iot2.user.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.DigestUtils;

import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;

class UserModelMapperTest {

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void toWeb(final boolean loginRequired) {
		final ModelMapper<User, UserModel> mapper = new UserModelMapper(loginRequired);
		final var password = random();
		final User user = new UserImpl(password, password, Optional.of("MD5"));

		final UserModel userModel = mapper.toWeb(user);

		assertEquals(IdUtil.getId(user), userModel.getId());
		assertEquals(user.name(), userModel.getName());
		assertEquals(loginRequired, userModel.isLoginRequired());
		assertEquals(DigestUtils.md5DigestAsHex(password.getBytes()), userModel.getPassword());
		assertEquals(user.algorithm().get(), userModel.getAlgorithm());
		assertFalse(userModel.isPasswordChanged());
		assertNull(userModel.getConfirmedPassword());
	}

	@Test
	void toWeb() {
		final ModelMapper<User, UserModel> mapper = new UserModelMapper(false);
		final var password = random();
		final User user = new UserImpl(password, password, Optional.empty());
		final UserModel userModel = mapper.toWeb(user);

		Hex.encodeHexString(password.getBytes());

		assertEquals(Hex.encodeHexString(password.getBytes()), userModel.getPassword());
		assertNull(userModel.getAlgorithm());
	}

	private String random() {
		return UUID.randomUUID().toString();
	}

}
