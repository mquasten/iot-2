package de.mq.iot2.batch.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mq.iot2.calendar.CalendarService;

@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2")
@EntityScan(basePackages = "de.mq.iot2")
@ComponentScan(basePackages = "de.mq.iot2")
@EnableTransactionManagement()
class SetupDatabaseImpl implements CommandLineRunner{

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);
	
	private final CalendarService calendarService;
	SetupDatabaseImpl(CalendarService calendarService) {
		this.calendarService = calendarService;
	}
	@Override
	public void run(String... args) throws Exception {
		LOG.info("Setup database DayGroups and Days.");
		calendarService.createDefaultCyclesGroupsAndDays();
		
	}
	

	

}
