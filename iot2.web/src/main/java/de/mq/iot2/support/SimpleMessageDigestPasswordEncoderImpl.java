package de.mq.iot2.support;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

class SimpleMessageDigestPasswordEncoderImpl implements PasswordEncoder {

	static final String DELIMITER = "-";

	/**
	 * Sinnlos, weiß nur Spring wozu das gut ist. Wird aber aufgerufen...
	 */
	@Override
	public String encode(final CharSequence rawPassword) {
		return Objects.requireNonNullElse(rawPassword, StringUtils.EMPTY).toString();
	}

	@Override
	public boolean matches(final CharSequence rawPassword, final String encodedPassword) {

		final String[] values = Objects.requireNonNullElse(encodedPassword, StringUtils.EMPTY).split(String.format("[%s]", DELIMITER), 2);
		final Optional<String> algorithm = algorithm(values);

		return values[0].equalsIgnoreCase(encode(rawPassword, algorithm));
	}

	private Optional<String> algorithm(final String[] values) {
		if (values.length <= 1) {
			return Optional.empty();
		}
		if (!org.springframework.util.StringUtils.hasText(values[1])) {
			return Optional.empty();
		}
		return Optional.of(values[1]);
	}

	private String encode(final CharSequence rawPassword, final Optional<String> algorithm) {
		if (algorithm.isEmpty()) {
			return Hex.encodeHexString(Objects.requireNonNullElse(rawPassword, StringUtils.EMPTY).toString().getBytes());
		}

		final DigestUtils digestUtils = new DigestUtils(algorithm.get());
		return digestUtils.digestAsHex(Objects.requireNonNullElse(rawPassword, StringUtils.EMPTY).toString());
	}

}
