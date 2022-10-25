package de.mq.iot2.calendar.support;

import java.math.BigInteger;
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
		super(dayGroup, new int[] { BigInteger.valueOf(offset).abs().intValueExact() }, new int[] { 1 }, offset, ENTITY_NAME.hashCode(), description);
		value();
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
		final var values = split(3);
		Assert.isTrue(values.length == 1, INVALID_VALUE_MESSAGE);
		final var date = easterdate(yearSupplier.get()).plusDays(signum() * values[0]);
		Assert.isTrue(yearSupplier.get().getValue() == date.getYear(), INVALID_VALUE_MESSAGE);

		return date;
	}

}
