package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

class SimpleMD5MessageDigestPasswordEncoderTest {

	private static final String PASSWORD = "James Clerk Maxwell";
	final PasswordEncoder passwordEncoder = new SimpleMD5MessageDigestPasswordEncoder();

	@Test
	void encode() {
		assertEquals(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()), passwordEncoder.encode(PASSWORD));
	}

	@Test
	void encodeNull() {
		assertEquals(DigestUtils.md5DigestAsHex(StringUtils.EMPTY.getBytes()), passwordEncoder.encode(null));
	}

	@Test
	void matches() {
		assertTrue(passwordEncoder.matches(PASSWORD, DigestUtils.md5DigestAsHex(PASSWORD.getBytes())));

	}

	@Test
	void matchesfalse() {
		assertFalse(passwordEncoder.matches(UUID.randomUUID().toString(), DigestUtils.md5DigestAsHex(PASSWORD.getBytes())));

	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = "")
	void matchesEmpty(final String value) {
		assertTrue(passwordEncoder.matches(value, DigestUtils.md5DigestAsHex(StringUtils.EMPTY.getBytes())));
	}
}
