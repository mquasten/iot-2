package de.mq.iot2.user.support;

import java.nio.charset.StandardCharsets;
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

@Entity(name = "User")
@Table(name = "LOGIN_USER")
class UserImpl implements User {
	final static String PASSWORD_DELIMIER = "-";

	@Id
	@Column(name = "ID", length = 36, nullable = false)
	private String id;

	@Column(name = "NAME", length = 25, nullable = false)
	private String name;
	@Column(name = "PASSWORD_HASH", length = 128, nullable = false)
	private String password;

	@Column(name = "ALGORITHM", length = 15)
	private String algorithm;

	@SuppressWarnings("unused")
	private UserImpl() {

	}

	UserImpl(final String name, final String rawPassword,final Optional<String> algorithm) {
		this( IdUtil.id() ,name, rawPassword, algorithm);
	}
	
	UserImpl(final long id, final String name, final String rawPassword,final Optional<String> algorithm) {
		this( IdUtil.id(id) ,name, rawPassword, algorithm);
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
