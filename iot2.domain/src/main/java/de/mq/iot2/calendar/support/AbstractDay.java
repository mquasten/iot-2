package de.mq.iot2.calendar.support;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.springframework.util.Assert;


@Entity(name = "Day")
@Table(name = "SPECIAL_DAY")
@Inheritance(strategy =InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE")
abstract class AbstractDay<T> implements Day<T>{
	
	static final String VALUE_REQUIRED_MESSAGE = "Value is required.";
	static final String ARRAY_INVALID_MESSAGE = "Alt least one value is required.";
	static final String INVALID_VALUE_MESSAGE = "Invalid value.";

	@Id
	private String id;
	
	@Column(name="VALUE")
	private Integer value;
	
	@Column(name="DESCRIPTION", length = 25)
	private String description;
	
	AbstractDay(final int[] values, final int[] digits, int typ, final String description) {
		arrayGuard(values);
		arrayGuard(digits);
		Assert.isTrue(values.length==digits.length ,"Arrays should have the same size.");
		final var stringBuilder = new StringBuilder();
		IntStream.range(0, values.length).forEach(i -> {
			String format = String.format("%"+ digits[i] +"d", values[i]).replace(' ', '0');
			stringBuilder.append(format);
		});
		value=Integer.parseInt(stringBuilder.toString());
		id=new UUID(typ, value).toString();
		this.description=description;
	}
	
	
	final int[] split(final int ... exp ) {
		Assert.notNull(value, VALUE_REQUIRED_MESSAGE);
		arrayGuard(exp);
		final int[] results = new int[exp.length+1];
		IntStream.range(0, exp.length).forEach(i -> {
			final int[] values = splitValue(i>0?results[i]:value, exp[i]);
			results[i]=values[0];
			results[i+1]=values[1];
		});
		return results;
		
	}


	private void arrayGuard(final int[] exp) {
		Assert.notNull(exp, ARRAY_INVALID_MESSAGE);
		Assert.isTrue(exp.length > 0, ARRAY_INVALID_MESSAGE);
	}
	
	private int[] splitValue(final int value, final int exp) {
		final var x = BigInteger.valueOf(10).pow(exp);
		final var result = BigInteger.valueOf(value).divide(x);
		final var rest = BigInteger.valueOf(value).subtract(result.multiply(x));
		return new int[] {result.intValueExact(), rest.intValueExact()};
	}
	
	public final Optional<String> description() {
		return Optional.ofNullable(description);
	}

}
