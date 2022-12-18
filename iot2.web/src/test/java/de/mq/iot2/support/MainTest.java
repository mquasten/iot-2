package de.mq.iot2.support;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;


class MainTest {

	@Test
	void main() {
		final String[] args = { "arg1", "arg2" };
		try (final var mocked = Mockito.mockStatic(SpringApplication.class)) {
			Main.main(args);
			mocked.verify(() -> SpringApplication.run(Main.class, args));
		}
	}
	
	@Test
	void newServingWebContentApplication() {
		BeanUtils.instantiateClass(Mockito.mock(Main.class).getClass());
	}

}
