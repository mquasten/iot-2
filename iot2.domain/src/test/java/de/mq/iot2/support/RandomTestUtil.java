package de.mq.iot2.support;

import java.util.Random;

import org.springframework.util.Assert;

public interface RandomTestUtil {
	
	static  int DEFAULT_STRING_LENGTH=100;
	
	public static String randomString() {
		return randomString(DEFAULT_STRING_LENGTH);
	}
	
	public static String randomString(final int length) {
		Assert.isTrue(length > 0, "Length should be > 0.");
		final var random = new Random();
		int leftLimit = 32; 
	    int rightLimit = 127; 
	  
	    return  random.ints(leftLimit, rightLimit + 1)
	      .limit(length)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString(); 
	}
	
	public static long randomLong() {
		final var random = new Random();
		return random.nextLong();
	}
	
	public static int randomInt() {
		final var random = new Random();
		return random.nextInt();
	}

}
