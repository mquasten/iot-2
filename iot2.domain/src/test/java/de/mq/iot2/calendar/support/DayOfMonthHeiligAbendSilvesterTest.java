package de.mq.iot2.calendar.support;

import java.time.MonthDay;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

class DayOfMonthHeiligAbendSilvesterTest {
	
	@Test
	void createdays() {
		final Cycle cycle = new CycleImpl(1L, "Freizeit" ,102, false);
		final DayGroup dayGroup = new DayGroupImpl(cycle, 6, "Urlaub Tarifvertrag");
		final  Collection<Day<?>>  days = List.of(new DayOfMonthImpl(dayGroup, MonthDay.of(12, 24), "Heiligabend" ), new  DayOfMonthImpl(dayGroup, MonthDay.of(12, 31), "Silvester" ));
		
        System.out.println( String.format("INSERT INTO DAY_GROUP(ID,NAME,READ_ONLY,CYCLE_ID) VALUES('%s','%s',1,'%s');",IdUtil.getId(dayGroup), dayGroup.name(), IdUtil.getId(dayGroup.cycle()) )) ;
		
        days.forEach(day ->  System.out.println(String.format("INSERT INTO SPECIAL_DAY(ID, DAY_TYPE, DAY_VALUE, DESCRIPTION, DAY_GROUP_ID) VALUES('%s', 'DayOfMonth', %s, '%s', '%s' );", IdUtil.getId(day), ReflectionTestUtils.getField(day, "value"), day.description().get(), IdUtil.getId(day.dayGroup()) )));
	    
	}
}
