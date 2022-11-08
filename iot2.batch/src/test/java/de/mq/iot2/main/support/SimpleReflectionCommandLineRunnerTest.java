package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Base64Utils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationUtils;


class SimpleReflectionCommandLineRunnerTest {
	
	private final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
	
	private final CommandLineRunner commandLineRunner = new SimpleReflectionCommandLineRunner(applicationContext); 
	
	interface TestService {
		void execute(final LocalDate date , final Long longValue );
	}
	
	@Test
	void run() throws Exception {
		final var service = Mockito.mock(TestService.class);
		Mockito.when(applicationContext.getBean(TestService.class)).thenReturn(service);
		final var methods =  ReflectionUtils.getDeclaredMethods(TestService.class);
		assertEquals(1, methods.length);
		final var date = LocalDate.of(1831, 6, 13);
		final var longValue = 4711L;
		final String arg =  Base64Utils.encodeToString(SerializationUtils.serialize(new ReflectionCommandLineRunnerArgumentsImpl(methods[0], new Object[] {date,longValue})));
		
		commandLineRunner.run(arg);
		
		Mockito.verify(service).execute(date, longValue);
	}
	
	

}


