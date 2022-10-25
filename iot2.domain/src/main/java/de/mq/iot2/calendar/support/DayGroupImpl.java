package de.mq.iot2.calendar.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;

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
		this.id = new UUID(id, id).toString();
	}

	DayGroupImpl(final String name) {
		this(name, true);
	}

	DayGroupImpl(final String name, boolean readolny) {
		Assert.notNull(name, "Name is required.");
		this.name = name;
		this.readOnly = readolny;
		this.id = new UUID(randomPositivLong(), System.currentTimeMillis()).toString();
	}

	private long randomPositivLong() {
		final var random = new Random();
		return random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@OneToMany(mappedBy = "dayGroup", targetEntity = AbstractDay.class, cascade = { CascadeType.ALL })
	private Collection<Day<?>> days = new HashSet<>();

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
	public final void assign(final Day<?> day) {
		days.add(day);
	}

	@Override
	public final void remove(final Day<?> day) {
		days.remove(day);
	}

	@Override
	public final Collection<Day<?>> days() {
		return Collections.unmodifiableCollection(days);
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
