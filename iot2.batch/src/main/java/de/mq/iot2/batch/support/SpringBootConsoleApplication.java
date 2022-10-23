package de.mq.iot2.batch.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.mq.iot2.calendar.CalendarService;

@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = "de.mq.iot2")
public class SpringBootConsoleApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	
	private final CalendarService calendarService;

	SpringBootConsoleApplication(final CalendarService calendarService) {
		this.calendarService=calendarService;
	}

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(SpringBootConsoleApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {

		LOG.info("EXECUTING : command line runner");

		calendarService.createDefaultGroupsAndDays();

	}


}
