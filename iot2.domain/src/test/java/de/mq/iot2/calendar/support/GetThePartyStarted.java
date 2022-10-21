package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class GetThePartyStarted {

	@Test
	void dayOfYear() {
		final int[] results = split(20221021, 4,2);
		assertEquals(3, results.length);
		assertEquals(2022,results[0]);
		assertEquals(10,results[1]);
		assertEquals(21,results[2]);
	}
	@Test
	void dayOfMonth() {
		final int[] results = split(1021, 2);
		assertEquals(2, results.length);
		assertEquals(10,results[0]);
		assertEquals(21,results[1]);
	}
	
	private int[] split(final int value, final int ... exp ) {
		atLeastOneExistsGuard(exp);
		final int[] results = new int[exp.length+1];
		IntStream.range(0, exp.length).forEach(i -> {
			final int[] values = split(i>0?results[i]:value, exp[i]);
			results[i]=values[0];
			results[i+1]=values[1];
		});
		return results;
		
	}

	private void atLeastOneExistsGuard(final int... exp) {
		if ( exp == null) {
			throw new IllegalArgumentException("Alt least one Exponent is required.");
		}
		if ( exp.length==0) {
			throw new IllegalArgumentException("Alt least one Exponent is required.");
		}
	}
	
	private int[] split(final int value, final int exp) {
		final var x = BigInteger.valueOf(10).pow(exp);
		final var result = BigInteger.valueOf(value).divide(x);
		final var rest = BigInteger.valueOf(value).subtract(result.multiply(x));
		return new int[] {result.intValueExact(), rest.intValueExact()};
	}

}
