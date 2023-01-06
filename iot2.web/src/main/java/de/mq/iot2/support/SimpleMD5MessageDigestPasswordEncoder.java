package de.mq.iot2.support;

import java.util.Objects;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.security.crypto.password.PasswordEncoder;



class SimpleMD5MessageDigestPasswordEncoder  implements PasswordEncoder {

	private final DigestUtils digestUtils = new DigestUtils("MD5");

	@Override
	public String encode(final CharSequence rawPassword) {
		final var pasword =  Objects.requireNonNullElse(rawPassword, StringUtils.EMPTY).toString();
		return digestUtils.digestAsHex(pasword);
	}

	@Override
	public boolean matches(final CharSequence rawPassword, String encodedPassword) {
		return Objects.requireNonNullElse(encodedPassword, StringUtils.EMPTY).equals(encode(rawPassword));
	}

}
