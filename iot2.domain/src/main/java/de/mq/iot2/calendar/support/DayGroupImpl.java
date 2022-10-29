package de.mq.iot2.calendar.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

@Entity(name = "DayGroup")
@Table(name = "DAY_GROUP")
class DayGroupImpl implements DayGroup {

	@Id
	@Column(name = "ID", length = 36)
	private String id;

	@Column(name = "NAME", length = 25)
	private String name;

	@Column(name = "READ_ONLY")
	private boolean readOnly;

	@SuppressWarnings("unused")
	private DayGroupImpl() {

	}

	DayGroupImpl(final long id, final String name) {
		this(id, name, true);
	}

	DayGroupImpl(final long id, final String name, boolean readolny) {
		Assert.notNull(name, "Name is required.");
		this.name = name;
		this.readOnly = readolny;
		this.id = IdUtil.id(id);
	}

	DayGroupImpl(final String name) {
		this(name, true);
	}

	DayGroupImpl(final String name, boolean readolny) {
		Assert.notNull(name, "Name is required.");
		this.name = name;
		this.readOnly = readolny;
		this.id = IdUtil.id();

	}

	@Override
	public final String name() {
		Assert.notNull(name, "Name is required.");
		return name;
	}

	@Override
	public final boolean readOnly() {
		return readOnly;
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
		if (!(obj instanceof DayGroupImpl)) {
			return super.equals(obj);

		}

		final var other = (DayGroupImpl) obj;

		if (StringUtils.hasText(other.name) && StringUtils.hasText(name)) {
			return name.equalsIgnoreCase(other.name);
		}
		return super.equals(obj);
	}

}
