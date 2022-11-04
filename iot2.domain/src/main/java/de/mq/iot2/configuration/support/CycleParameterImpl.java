package de.mq.iot2.configuration.support;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.configuration.Configuration;
import de.mq.iot2.configuration.CycleParameter;

@Entity(name = "CycleParameter")
@Table(name = "CYCLE_PARAMETER")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
class CycleParameterImpl extends ParameterImpl implements CycleParameter {

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
	public Cycle cycle() {
		cycleRequiredGuard(cycle);
		return cycle;
	}

}
