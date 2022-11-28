package de.mq.iot2.main.support;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;

@Service
class DayGroupBatchImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(DayGroupBatchImpl.class);
	private final CalendarService calendarService;
	
	DayGroupBatchImpl(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	@BatchMethod(value = "add-local-date", converterClass = DayGroupBatchConverterImpl.class)
	final void addLocalDate(final String dayGroup, final LocalDate fromDate, final LocalDate toDate) {
		
	   final int numberOfDays =  calendarService.addLocalDateDays(dayGroup, fromDate, toDate);
	   LOGGER.info("{} days added to DayGroup {}." , numberOfDays, dayGroup);
	   
	}
	
	@BatchMethod(value = "delete-local-date", converterClass = DayGroupBatchConverterImpl.class)
	final void deleteLocalDate(final String dayGroup, final LocalDate fromDate, final LocalDate toDate) {
		 final int numberOfDays =  calendarService.deleteLocalDateDays(dayGroup, fromDate, toDate);
		 LOGGER.info("{} days deleted from DayGroup {}." , numberOfDays, dayGroup);
	}

}
