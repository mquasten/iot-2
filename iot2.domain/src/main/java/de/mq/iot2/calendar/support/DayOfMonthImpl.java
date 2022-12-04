package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.time.MonthDay;



import org.springframework.util.Assert;

import de.mq.iot2.calendar.DayGroup;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = DayOfMonthImpl.ENTITY_NAME)
@DiscriminatorValue(DayOfMonthImpl.ENTITY_NAME)
class DayOfMonthImpl extends AbstractDay<MonthDay> {
	static final String ENTITY_NAME = "DayOfMonth";

	@SuppressWarnings("unused")
	private DayOfMonthImpl() {
		super();
	}

	DayOfMonthImpl(final DayGroup dayGroup, final MonthDay monthDay) {
		this(dayGroup, monthDay, null);
	}

	DayOfMonthImpl(final DayGroup dayGroup, final MonthDay monthDay, final String description) {
		super(dayGroup, toArray(monthDay), new int[] { 2, 2 }, SIGNUM_POSITIV_INT, ENTITY_NAME, description);

	}

	private static int[] toArray(final MonthDay monthDay) {
		Assert.notNull(monthDay, VALUE_REQUIRED_MESSAGE);
		return new int[] { monthDay.getMonthValue(), monthDay.getDayOfMonth() };
	}

	@Override
	public boolean matches(final LocalDate date) {
		Assert.notNull(date, VALUE_REQUIRED_MESSAGE);
		return MonthDay.of(date.getMonth(), date.getDayOfMonth()).equals(value());
	}

	@Override
	public MonthDay value() {
		final var values = split(2);
		Assert.isTrue(values.length == 2, INVALID_VALUE_MESSAGE);
		return MonthDay.of(values[0], values[1]);
	}

}
