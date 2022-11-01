package de.mq.iot2.configuration.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.Assert;

import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.support.IdUtil;

@Entity(name = "Configuration")
@Table(name = "CONFIGURATION")
class ConfigurationImpl implements Configuration {
	@Id
	@Column(name = "ID", length = 36, nullable = false)
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "RULE_KEY", length = 15, nullable = false)
	private RuleKey key;

	@Column(name = "NAME", length = 25, nullable = false)
	private String name;

	@SuppressWarnings("unused")
	private ConfigurationImpl() {

	}

	ConfigurationImpl(final long id, final RuleKey key, final String name) {
		this(IdUtil.id(id), key, name);
	}

	ConfigurationImpl(final RuleKey key, final String name) {
		this(IdUtil.id(), key, name);
	}

	private ConfigurationImpl(final String id, final RuleKey key, final String name) {
		keyRequiredGuard(key);
		nameRequiredGuard(name);
		this.name = name;
		this.key = key;
		this.id = id;
	}

	private void keyRequiredGuard(final RuleKey key) {
		Assert.notNull(key, "Key is required.");
	}

	private void nameRequiredGuard(final String name) {
		Assert.notNull(name, "Name is required.");
	}

	@Override
	public RuleKey key() {
		keyRequiredGuard(key);
		return key;
	}

	@Override
	public String name() {
		return name != null ? name : "";
	}

	@Override
	public int hashCode() {
		if (key == null) {
			return super.hashCode();
		}
		return key.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ConfigurationImpl)) {
			return super.equals(obj);

		}

		final var other = (ConfigurationImpl) obj;

		if ((other.key != null) && (key != null)) {
			return key == other.key;
		}
		return super.equals(obj);
	}

}
