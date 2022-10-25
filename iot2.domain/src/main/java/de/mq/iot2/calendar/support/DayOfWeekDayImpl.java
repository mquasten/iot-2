package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.persistence.Entity;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.DayGroup;

@Entity(name = DayOfWeekDayImpl.ENTITY_NAME)
class DayOfWeekDayImpl extends AbstractDay<DayOfWeek> {

	static final String ENTITY_NAME = "DayOfWeekDay";

	@SuppressWarnings("unused")
	private DayOfWeekDayImpl() {
		super();
	}

	DayOfWeekDayImpl(final DayGroup dayGroup, final DayOfWeek dayOfWeek) {
		this(dayGroup, dayOfWeek, null);
	}

	DayOfWeekDayImpl(final DayGroup dayGroup, final DayOfWeek dayOfWeek, final String description) {
		super(dayGroup, toArray(dayOfWeek), new int[] { 1 }, SIGNUM_POSITIV_INT, ENTITY_NAME.hashCode(), description);
	}

	private static int[] toArray(final DayOfWeek dayOfWeek) {
		Assert.notNull(dayOfWeek, INVALID_VALUE_MESSAGE);
		return new int[] { dayOfWeek.getValue() };
	}

	@Override
	public final boolean matches(final LocalDate date) {
		Assert.notNull(date, VALUE_REQUIRED_MESSAGE);
		return value() == date.getDayOfWeek();
	}

	@Override
	public final DayOfWeek value() {
		final var values = split(1);
		Assert.isTrue(values.length == 1, INVALID_VALUE_MESSAGE);
		return DayOfWeek.of(values[0]);
	}

}
