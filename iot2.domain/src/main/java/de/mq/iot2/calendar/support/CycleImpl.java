package de.mq.iot2.calendar.support;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.support.IdUtil;

@Entity(name = "Cycle")
public class CycleImpl implements Cycle {
	private static final String NAME_IS_REQUIRED_MESSAGE = "Name is required.";

	private static final String ID_IS_REQUIRED_MESSAGE = "Id is required.";
	@Id
	@Column(name = "ID", length = 36, nullable = false)
	private String id;
	@Column(name = "NAME", length = 25, nullable = false)
	private String name;
	@Column(name = "PRIORITY", nullable = false)
	private Integer priority;

	@Column(name = "DEFAULT_CYCLE", nullable = false)
	private boolean defaultCycle;

	@SuppressWarnings("unused")
	private CycleImpl() {

	}

	CycleImpl(final long id, final String name, final int priority, final boolean defaultCycle) {
		this(IdUtil.id(id), name, priority, defaultCycle);
	}

	CycleImpl(final long id, final String name, final int priority) {
		this(IdUtil.id(id), name, priority, false);
	}

	CycleImpl(final String name, final int priority, final boolean defaultCycle) {
		this(IdUtil.id(), name, priority, defaultCycle);
	}

	CycleImpl(final String name, final int priority) {
		this(IdUtil.id(), name, priority, false);
	}

	private CycleImpl(final String id, final String name, final int priority, final boolean defaultCycle) {
		nameNotEmptyGuard(name);
		this.id = id;
		this.name = name;
		this.priority = priority;
		this.defaultCycle = defaultCycle;
	}

	@Override
	public String name() {
		return name != null ? name : "";

	}

	private void nameNotEmptyGuard(final String name) {
		Assert.hasText(name, NAME_IS_REQUIRED_MESSAGE);
	}

	@Override
	public int priority() {
		return priority != null ? priority : Integer.MAX_VALUE;

	}

	@Override
	public boolean isDeaultCycle() {
		return defaultCycle;
	}

	@Override
	public UUID id() {
		Assert.notNull(id, ID_IS_REQUIRED_MESSAGE);
		return UUID.fromString(id);
	}

	@Override
	public int hashCode() {
		if (name == null) {
			return super.hashCode();
		}
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof CycleImpl)) {
			return super.equals(object);

		}
		final var other = (CycleImpl) object;

		if ((name == null) || (other.name == null)) {
			return super.equals(object);
		}

		return other.name.equals(name);
	}

}
