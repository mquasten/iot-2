package de.mq.iot2.main.support;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.calendar.CalendarService;

class DayGroupBatchImplTest {

	private static final LocalDate FROM_DATE = LocalDate.now();
	private static final LocalDate TO_DATE = LocalDate.now().plusDays(10);

	private static final String DAY_GROUP = "Group";

	private final CalendarService calendarService = Mockito.mock(CalendarService.class);

	private final DayGroupBatchImpl dayGroupBatch = new DayGroupBatchImpl(calendarService);

	@Test
	void addLocalDate() {
		dayGroupBatch.addLocalDate(DAY_GROUP, FROM_DATE, TO_DATE);

		Mockito.verify(calendarService).addLocalDateDays(DAY_GROUP, FROM_DATE, TO_DATE);
	}

	@Test
	void deleteLocalDate() {
		dayGroupBatch.deleteLocalDate(DAY_GROUP, FROM_DATE, TO_DATE);

		Mockito.verify(calendarService).deleteLocalDateDays(DAY_GROUP, FROM_DATE, TO_DATE);
	}

}
