package de.mq.iot2.calendar.support;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

@Entity(name = AbstractDay.ENTITY_NAME)
@Table(name = "SPECIAL_DAY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DAY_TYPE", length = 15)
abstract class AbstractDay<T> implements Day<T> {

	private static final String ARRAYS_DIFFERENT_SIZE_MESSAGE = "Arrays should have the same size.";
	static final String VALUE_REQUIRED_MESSAGE = "Value is required.";
	static final String ARRAY_INVALID_MESSAGE = "Alt least one value is required.";
	static final String INVALID_VALUE_MESSAGE = "Invalid value.";

	static final String ENTITY_NAME = "Day";
	static final int SIGNUM_POSITIV_INT = 1;
	@Id
	@Column(name = "ID", length = 36, nullable = false)
	private String id;

	@Column(name = "DAY_VALUE", nullable = false)
	private Integer value;

	@Column(name = "DESCRIPTION", length = 25)
	private String description;

	@ManyToOne(targetEntity = DayGroupImpl.class)
	@JoinColumn(name = "DAY_GROUP_ID", nullable = false)
	private DayGroup dayGroup;

	AbstractDay() {

	}

	AbstractDay(final DayGroup dayGroup, final int[] values, final int[] digits, final int signum, final String typ, final String description) {
		Assert.notNull(dayGroup, VALUE_REQUIRED_MESSAGE);
		arrayGuard(values);
		arrayMemberMinVauleGuard(values, 0);
		arrayGuard(digits);
		arrayMemberMinVauleGuard(digits, 1);
		Assert.isTrue(values.length == digits.length, ARRAYS_DIFFERENT_SIZE_MESSAGE);
		final var stringBuilder = new StringBuilder();
		IntStream.range(0, values.length).forEach(i -> {
			String format = String.format("%" + digits[i] + "d", values[i]).replace(' ', '0');
			stringBuilder.append(format);
		});

		value = BigInteger.valueOf(signum).signum() * Integer.parseInt(stringBuilder.toString());
		id = IdUtil.id(value, typ);
		this.description = description;
		this.dayGroup = dayGroup;
	}

	final int[] split(final int... exp) {
		Assert.notNull(value, VALUE_REQUIRED_MESSAGE);
		arrayGuard(exp);
		arrayMemberMinVauleGuard(exp, 0);
		final int value = BigInteger.valueOf(this.value).abs().intValueExact();

		if (BigInteger.valueOf(10).pow(IntStream.of(exp).sum()).intValueExact() > value) {
			return new int[] { value };
		}
		final int[] results = new int[exp.length + 1];
		IntStream.range(0, exp.length).forEach(i -> {
			final int[] values = splitValue(i > 0 ? results[i] : value, exp[i]);
			results[i] = values[0];
			results[i + 1] = values[1];
		});
		return results;

	}

	private void arrayGuard(final int[] exp) {
		Assert.notNull(exp, ARRAY_INVALID_MESSAGE);
		Assert.isTrue(exp.length > 0, ARRAY_INVALID_MESSAGE);
	}

	private void arrayMemberMinVauleGuard(final int[] exp, final int limit) {
		IntStream.of(exp).min().ifPresent(min -> Assert.isTrue(min >= limit, INVALID_VALUE_MESSAGE));
	}

	private int[] splitValue(final int value, final int exp) {
		final var x = BigInteger.valueOf(10).pow(exp);
		final var result = BigInteger.valueOf(value).divide(x);
		final var rest = BigInteger.valueOf(value).subtract(result.multiply(x));
		return new int[] { result.intValueExact(), rest.intValueExact() };
	}

	public final Optional<String> description() {
		return Optional.ofNullable(description);
	}

	int signum() {
		return BigInteger.valueOf(value).signum();
	}

	@Override
	public final int hashCode() {
		if (value == null) {
			return System.identityHashCode(this);
		}
		return getClass().hashCode() + value.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {

		if (!(object instanceof AbstractDay)) {
			return super.equals(object);

		}
		final var other = (AbstractDay<?>) object;

		if ((value == null) || (other.value == null)) {
			return super.equals(object);
		}

		return other.getClass().equals(getClass()) && other.value.intValue() == value.intValue();
	}

	@Override
	public DayGroup dayGroup() {
		return dayGroup;

	}
}
