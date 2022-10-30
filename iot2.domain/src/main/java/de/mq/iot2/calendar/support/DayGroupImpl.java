package de.mq.iot2.calendar.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

@Entity(name = "DayGroup")
@Table(name = "DAY_GROUP")
class DayGroupImpl implements DayGroup {

	@Id
	@Column(name = "ID", length = 36, nullable = false)
	private String id;

	@Column(name = "NAME", length = 25, nullable = false)
	private String name;

	@Column(name = "READ_ONLY", nullable = false)
	private boolean readOnly;
	
	@ManyToOne(targetEntity = CycleImpl.class)
	@JoinColumn(name = "CYCLE_ID", nullable = false)
	private Cycle cycle;

	@SuppressWarnings("unused")
	private DayGroupImpl() {

	}

	DayGroupImpl(final Cycle cycle, final long id, final String name) {
		this(cycle,id, name, true);
	}

	DayGroupImpl(final Cycle cycle, final long id, final String name, boolean readolny) {
		this(cycle, IdUtil.id(id), name, readolny);
	}

	DayGroupImpl(final Cycle cycle, final String name) {
		this(cycle,name, true);
	}

	DayGroupImpl(final Cycle cycle,final String name, boolean readolny) {
		this(cycle, IdUtil.id(), name, readolny);
	}
	
	private DayGroupImpl(final Cycle cycle, final String id, final String name, boolean readolny) {
		cycleRequiredGuard(cycle);
		nameRequiredGuard(name);
		this.name = name;
		this.readOnly = readolny;
		this.id = id;
		this.cycle=cycle;
	}

	private void cycleRequiredGuard(final Cycle cycle) {
		Assert.notNull(cycle, "Cycle is required.");
	}

	private void nameRequiredGuard(final String name) {
		Assert.hasText(name, "Name is required.");
	}

	@Override
	public final String name() {
		return name!=null ? name : "";
	}

	@Override
	public final boolean readOnly() {
		return readOnly;
	}
	@Override
	public final Cycle cycle() {
		return cycle;
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
