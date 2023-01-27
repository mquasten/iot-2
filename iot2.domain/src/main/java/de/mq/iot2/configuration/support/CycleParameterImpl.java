package de.mq.iot2.configuration.support;



import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.support.CycleImpl;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.CycleParameter;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Entity(name = "CycleParameter")
@DiscriminatorValue("CycleParameter")
class CycleParameterImpl extends AbstractParameter implements CycleParameter {

	@ManyToOne(targetEntity = CycleImpl.class)
	@JoinColumn(name = "CYCLE_ID", nullable = false)
	@NotNull
	@Valid
	private Cycle cycle;

	@SuppressWarnings("unused")
	private CycleParameterImpl() {

	}

	CycleParameterImpl(final Configuration configuration, final Key key, final String value, final Cycle cycle) {
		super(configuration, key, value);
		cycleRequiredGuard(cycle);
		this.cycle = cycle;
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
