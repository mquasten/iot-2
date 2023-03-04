package de.mq.iot2.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Security;
import java.util.Collection;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.password.PasswordEncoder;

class SimpleMessageDigestPasswordEncoderImplTest {

	private static final String PASSWORD = "James Clerk Maxwell";
	final PasswordEncoder passwordEncoder = new SimpleMessageDigestPasswordEncoderImpl();

	@Test
	void encode() {
		assertEquals(PASSWORD, passwordEncoder.encode(PASSWORD));
	}

	@Test
	void encodeNull() {
		assertEquals(StringUtils.EMPTY, passwordEncoder.encode(null));
	}

	@ParameterizedTest
	@MethodSource("algorithms")
	void matches(final String algorithm) {
		final String encodedPassword = String.format("%s" + SimpleMessageDigestPasswordEncoderImpl.DELIMITER + "%s", new DigestUtils(algorithm).digestAsHex(PASSWORD.getBytes()),
				algorithm);
		assertTrue(passwordEncoder.matches(PASSWORD, encodedPassword));
	}

	@Test
	void matches() {
		final var algorithm = "MD5";
		final String encodedPassword = String.format("%s" + SimpleMessageDigestPasswordEncoderImpl.DELIMITER + "%s", new DigestUtils(algorithm).digestAsHex(PASSWORD.getBytes()),
				algorithm);
		assertFalse(passwordEncoder.matches(PASSWORD.split("[ ]")[0], encodedPassword));
	}

	static Collection<String> algorithms() {
		return Security.getAlgorithms("MessageDigest");
	}

	@Test
	void matchesNotCryted() {
		assertTrue(passwordEncoder.matches(PASSWORD, Hex.encodeHexString(PASSWORD.getBytes())));
	}

	@Test
	void matchesNotCrytedAlgorithmEmpty() {
		assertTrue(passwordEncoder.matches(PASSWORD, Hex.encodeHexString(PASSWORD.getBytes()) + "-"));
	}

	@Test
	void nullValues() {
		assertTrue(passwordEncoder.matches(null, null));
	}

}
