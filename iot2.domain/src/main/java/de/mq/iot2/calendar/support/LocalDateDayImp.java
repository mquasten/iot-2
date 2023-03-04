package de.mq.iot2.calendar.support;

import java.math.BigInteger;
import java.time.LocalDate;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.DayGroup;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = LocalDateDayImp.ENTITY_NAME)
@DiscriminatorValue(LocalDateDayImp.ENTITY_NAME)
class LocalDateDayImp extends AbstractDay<LocalDate> {
	static final String ENTITY_NAME = "LocaldateDay";

	@SuppressWarnings("unused")
	private LocalDateDayImp() {

	}

	LocalDateDayImp(final DayGroup dayGroup, final LocalDate date, final String description) {
		super(dayGroup, toArray(date), new int[] { 4, 2, 2 }, date.getYear(), ENTITY_NAME, description);
	}

	LocalDateDayImp(final DayGroup dayGroup, final LocalDate date) {
		this(dayGroup, date, null);
	}

	private static int[] toArray(final LocalDate date) {
		Assert.notNull(date, VALUE_REQUIRED_MESSAGE);
		return new int[] { BigInteger.valueOf(date.getYear()).abs().intValueExact(), date.getMonthValue(), date.getDayOfMonth() };
	}

	@Override
	public boolean matches(final LocalDate date) {
		return date.equals(value());
	}

	@Override
	public LocalDate value() {
		final var values = split(4, 2);
		Assert.isTrue(values.length == 3, INVALID_VALUE_MESSAGE);
		return LocalDate.of(signum() * values[0], values[1], values[2]);
	}
}
