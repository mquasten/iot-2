package de.mq.iot2.user.support;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity(name = "User")
@Table(name = "LOGIN_USER")
class UserImpl implements User {
	static final String LANGUAGE_INVALID_MESSAGE = "Language %s is invalid.";

	final static String PASSWORD_DELIMIER = "-";

	@Id
	@Column(name = "ID", length = 36, nullable = false)
	@Size(min = 36, max = 36)
	private String id;

	@Column(name = "NAME", length = 25, nullable = false)
	@Size(max = 25)
	@NotBlank
	private String name;
	@Column(name = "PASSWORD_HASH", length = 128, nullable = false)
	@Size(max = 128)
	@NotBlank
	private String password;

	@Column(name = "ALGORITHM", length = 15)
	@Size(max = 15)
	private String algorithm;

	@Column(name = "LANGUAGE", length = 2)
	@Size(max = 2)
	private String language;

	@SuppressWarnings("unused")
	private UserImpl() {

	}

	UserImpl(final String name, final String rawPassword, final Optional<String> algorithm) {
		this(IdUtil.id(), name, rawPassword, algorithm);
	}

	UserImpl(final long id, final String name, final String rawPassword, final Optional<String> algorithm) {
		this(IdUtil.id(id), name, rawPassword, algorithm);
	}

	private UserImpl(final String id, final String name, final String rawPassword, final Optional<String> algorithm) {
		this.id = id;
		nameRequiredGuard(name);
		Assert.notNull(algorithm, "Algorithm should not be null.");
		this.name = name;

		algorithm.ifPresentOrElse(value -> assingPassword(rawPassword, value), () -> assingPassword(rawPassword));
	}

	private void nameRequiredGuard(final String name) {
		Assert.hasText(name, "Name is required.");
	}

	@Override
	public void assingPassword(final String rawPassword, final String algorithm) {
		passwordRequiredGuard(rawPassword);
		Assert.hasText(algorithm, "Algorithm should not be null.");

		password = new DigestUtils(algorithm).digestAsHex(rawPassword);
		this.algorithm = algorithm;
	}

	private void passwordRequiredGuard(final String rawPassword) {
		Assert.hasText(rawPassword, "Password is required.");
	}

	@Override
	public void assingPassword(final String rawPassword) {
		passwordRequiredGuard(rawPassword);
		password = Hex.encodeHexString(rawPassword.getBytes(StandardCharsets.UTF_8));
		algorithm = null;
	}

	@Override
	public String name() {
		nameRequiredGuard(name);
		return name;
	}

	@Override
	public String encodedPasswordAndAlgorithm() {
		passwordRequiredGuard(password);
		return StringUtils.hasText(algorithm) ? String.format("%s%s%s", password, PASSWORD_DELIMIER, algorithm) : password;
	}

	@Override
	public String encodedPassword() {
		passwordRequiredGuard(password);
		return password;
	}

	@Override
	public final Optional<String> algorithm() {
		return StringUtils.hasText(algorithm) ? Optional.of(algorithm) : Optional.empty();
	}

	@Override
	public final Optional<Locale> language() {
		if (!StringUtils.hasText(language)) {
			return Optional.empty();
		}
		validLanguageGuard(language);
		return Optional.of(Locale.of(language));
	}

	private void validLanguageGuard(final String language) {
		Assert.isTrue(Arrays.asList(Locale.getISOLanguages()).contains(language), String.format(LANGUAGE_INVALID_MESSAGE, language));
	}

	@Override
	public final void assignLanguage(final Locale locale) {
		Assert.notNull(locale, "Locale is required");
		validLanguageGuard(locale.getLanguage());
		language = locale.getLanguage();
	}

	@Override
	public int hashCode() {
		if (name == null) {
			return super.hashCode();
		}
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof UserImpl)) {
			return super.equals(obj);
		}

		final var other = (UserImpl) obj;
		if (StringUtils.hasText(other.name) && StringUtils.hasText(name)) {
			return name.equalsIgnoreCase(other.name);
		}
		return super.equals(obj);
	}

}
