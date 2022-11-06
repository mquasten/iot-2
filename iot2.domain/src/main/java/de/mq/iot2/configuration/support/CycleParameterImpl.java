package de.mq.iot2.configuration.support;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleImpl;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.CycleParameter;
import de.mq.iot2.support.IdUtil;

@Entity(name = "CycleParameter")
@Table(name = "CYCLE_PARAMETER")
@DiscriminatorValue("CycleParameter")
class CycleParameterImpl extends AbstractParameter implements CycleParameter {

	@ManyToOne(targetEntity = CycleImpl.class)
	@JoinColumn(name = "CYCLE_ID", nullable = false)
	private Cycle cycle;

	@SuppressWarnings("unused")
	private CycleParameterImpl() {

	}

	CycleParameterImpl(final Configuration configuration, final Key key, final String value, final Cycle cycle) {
		super(id(configuration, cycle, key), configuration, key, value);
		cycleRequiredGuard(cycle);
		this.cycle = cycle;
	}

	private static String id(final Configuration configuration, Cycle cycle, final Key key) {
		Assert.notNull(configuration, "Configuration is required.");
		Assert.notNull(cycle, "Cycle is required.");
		final var configurationId = configuration.id();
		final var cycleId = cycle.id();
		return IdUtil.id(configurationId.getLeastSignificantBits() + configurationId.getMostSignificantBits() + cycleId.getLeastSignificantBits() + cycleId.getMostSignificantBits(), key.name());
	}

	private void cycleRequiredGuard(final Cycle cycle) {
		Assert.notNull(cycle, "Cycle required.");
	}

	@Override
	public final Cycle cycle() {
		cycleRequiredGuard(cycle);
		return cycle;
	}

	@Override
	public final int hashCode() {

		if (cycle == null) {
			return super.hashCode();
		}
		return super.hashCode() + cycle.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (!(object instanceof CycleParameterImpl)) {
			return false;
		}
		final var other = (CycleParameterImpl) object;
		if ((other.cycle == null) || (cycle == null)) {
			return false;
		}
		return super.equals(object) && other.cycle.equals(cycle);
	}

}
