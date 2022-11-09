package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;

@Service
public class EndOfDayBatchImpl {

	private final CalendarService calendarService;
	
	private final ConfigurationService configurationService;

	EndOfDayBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService=configurationService;
	}

	@BatchMethod(value = "end-of-day", converterClass = EndOfDayBatchArgumentConverterImpl.class)
	final void execute(final LocalDate date) {

		System.out.println("Use date:" + date);

		final var cycle = calendarService.cycle(date);

		System.out.println(cycle);
		
		Map<Key,? extends Object> results = configurationService.parameters(RuleKey.EndOfDay, cycle);
		
		results.entrySet().forEach(e -> System.out.println(e.getKey() + "="+ e.getValue()));
		
		

	}

}
