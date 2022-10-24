package de.mq.iot2.calendar.support;

import java.time.LocalDate;
import java.time.Year;
import java.util.function.Supplier;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.DayGroup;

@Entity(name = GaussDayImpl.ENTITY_NAME)
class GaussDayImpl extends AbstractDay<LocalDate> {

	static final String ENTITY_NAME = "GaussDay";

	@Transient
	private final transient Supplier<Year> yearSupplier = () -> Year.now();

	@SuppressWarnings("unused")
	private GaussDayImpl() {
		super();
	}

	GaussDayImpl(final DayGroup dayGroup, final int offset) {
		this(dayGroup, offset, null);
	}

	GaussDayImpl(final DayGroup dayGroup, final int offset, final String description) {
		super(dayGroup, toArray(offset), new int[] { 1 }, ENTITY_NAME.hashCode(), description);
	}

	private static int[] toArray(final int offset) {
		valueGuard(offset);
		return new int[] { offset };
	}

	private LocalDate easterdate(final Year year) {

		final int k = year.getValue() / 100;
		final int m = 15 + (3 * k + 3) / 4 - (8 * k + 13) / 25;
		final int s = 2 - (3 * k + 3) / 4;
		final int a = year.getValue() % 19;
		final int d = (19 * a + m) % 30;
		final int r = (d + a / 11) / 29;
		final int og = 21 + d - r;
		final int sz = 7 - (year.getValue() + year.getValue() / 4 + s) % 7;
		final int oe = 7 - (og - sz) % 7;
		final int daysFromFirstOfMarch = og + oe;
		return LocalDate.of(year.getValue(), 3, 1).minusDays(1).plusDays(daysFromFirstOfMarch);
	}

	@Override
	public boolean matches(final LocalDate date) {
		return date.equals(value());
	}

	@Override
	public LocalDate value() {
		final var values = split(2);
		Assert.isTrue(values.length == 1, INVALID_VALUE_MESSAGE);
		valueGuard(values[0]);
		return easterdate(yearSupplier.get()).plusDays(values[0]);
	}

	private static void valueGuard(final double value) {
		Assert.isTrue(Math.abs(value) <= 99, INVALID_VALUE_MESSAGE);
	}

}
