package de.mq.iot2.calendar.support;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity(name = "DayGroup")
@Table(name = "DAY_GROUP")
class DayGroupImpl implements DayGroup {

	@Id
	@Column(name = "ID", length = 36, nullable = false)
	@Size(min = 36, max = 36)
	private String id;

	@Column(name = "NAME", length = 25, nullable = false)
	@NotBlank
	@Size(max = 25)
	private String name;

	@Column(name = "READ_ONLY", nullable = false)
	private boolean readOnly;

	@ManyToOne(targetEntity = CycleImpl.class)
	@JoinColumn(name = "CYCLE_ID", nullable = false)
	@NotNull
	@Valid
	private Cycle cycle;

	@SuppressWarnings("unused")
	private DayGroupImpl() {

	}

	DayGroupImpl(final Cycle cycle, final long id, final String name) {
		this(cycle, id, name, true);
	}

	DayGroupImpl(final Cycle cycle, final long id, final String name, boolean readolny) {
		this(cycle, IdUtil.id(id), name, readolny);
	}

	DayGroupImpl(final Cycle cycle, final String name) {
		this(cycle, name, true);
	}

	DayGroupImpl(final Cycle cycle, final String name, boolean readolny) {
		this(cycle, IdUtil.id(), name, readolny);
	}

	private DayGroupImpl(final Cycle cycle, final String id, final String name, boolean readolny) {
		cycleRequiredGuard(cycle);
		nameRequiredGuard(name);
		this.name = name;
		this.readOnly = readolny;
		this.id = id;
		this.cycle = cycle;
	}

	private void cycleRequiredGuard(final Cycle cycle) {
		Assert.notNull(cycle, "Cycle is required.");
	}

	private void nameRequiredGuard(final String name) {
		Assert.hasText(name, "Name is required.");
	}

	@Override
	public final String name() {
		return name != null ? name : "";
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
