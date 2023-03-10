package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileCopyUtils;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;

class Array2DayConverterImplTest {
	
	private final Converter<Pair<String[], Pair<Map<String, DayGroup>, Map<String, Cycle>>>, Day<?>> converter = new  Array2DayConverterImpl(new DefaultConversionService());
	
	@Test
	void convertGausDayFull() throws IOException {
		final String[] columns =getLineFromFile(0);
		final Day<?> day = converter.convert(Pair.of(columns, Pair.of(Map.of(), Map.of())));
			
		assertTrue(day instanceof GaussDayImpl);
		assertEquals(Integer.parseInt(columns[1]) , ReflectionTestUtils.getField(day, "value"));
		assertEquals(columns[2], day.description().orElseThrow());
		assertEquals(IdUtil.id(Long.parseLong(columns[3])), IdUtil.getId(day.dayGroup()));
		assertEquals(columns[4], day.dayGroup().name());
		assertEquals(Boolean.parseBoolean(columns[5]), day.dayGroup().readOnly());	
		assertEquals(IdUtil.id(Long.parseLong(columns[6])), IdUtil.getId(day.dayGroup().cycle()));
		assertEquals(columns[7], day.dayGroup().cycle().name());
		assertEquals(Integer.parseInt(columns[8]),day.dayGroup().cycle().priority());
		assertEquals(Boolean.parseBoolean(columns[9].strip()),day.dayGroup().cycle().isDeaultCycle());
	}

	private String[] getLineFromFile(int lineNumber) throws IOException {
		try (final InputStream is = getClass().getClassLoader().getResourceAsStream("calendar.csv")) {
			final List<String[]> data = List.of(new String(FileCopyUtils.copyToByteArray(is)).split("\n", -1)).stream().map(line -> line.split("[;]")).collect(Collectors.toList());
			return data.get(lineNumber);
		}
	}
	
	@Test
	void convertWrongNumberOfColumns(){
		assertEquals(Array2DayConverterImpl.WRONG_NUMBER_OF_COLUMNS_MESSAGE, assertThrows(IllegalArgumentException.class, () -> converter.convert(Pair.of(new String[] {"x;y"}, Pair.of(Map.of(), Map.of())))).getMessage());
	}

	@Test
	void convertDayOfMonthExistingDataGroup() throws IOException {
		final String cycleId = IdUtil.id(1L);
		final Cycle cycle = Mockito.mock(CycleImpl.class);
		IdUtil.assignId(cycle, cycleId);
		
		final String dayGroupId = IdUtil.id(1L);
		final DayGroup dayGroup = Mockito.mock(DayGroupImpl.class);
		IdUtil.assignId(dayGroup, dayGroupId);
	
		
		final String[] columns =getLineFromFile(1);
		final Day<?> day = converter.convert(Pair.of(columns, Pair.of(Map.of(dayGroupId, dayGroup), Map.of(cycleId, cycle))));
		
		assertEquals(dayGroup, day.dayGroup());
		assertTrue(day instanceof DayOfMonthImpl);
		assertEquals( MonthDay.of(5, 1) , day.value());
		assertEquals(columns[2], day.description().orElseThrow());
	}
	
	@Test
	void convertDayOfMonthInvalidValue() throws IOException {
		final String cycleId = IdUtil.id(1L);
		final Cycle cycle = Mockito.mock(CycleImpl.class);
		IdUtil.assignId(cycle, cycleId);
		
		final String dayGroupId = IdUtil.id(1L);
		final DayGroup dayGroup = Mockito.mock(DayGroupImpl.class);
		IdUtil.assignId(dayGroup, dayGroupId);
	
		final String[] columns =getLineFromFile(1);
		columns[1]="1";
		
		assertEquals(String.format(Array2DayConverterImpl.INVALID_MONTH_DAY_MESSAGE, columns[1]), assertThrows(IllegalArgumentException.class, () -> converter.convert(Pair.of(columns, Pair.of(Map.of(dayGroupId, dayGroup), Map.of(cycleId, cycle))))).getMessage());
		
	}
	
	@Test
	void convertDayOfWeek() throws IOException {
		final String cycleId = IdUtil.id(1L);
		final Cycle cycle = Mockito.mock(CycleImpl.class);
		IdUtil.assignId(cycle, cycleId);
		
		final String dayGroupId = IdUtil.id(2L);
		final DayGroup dayGroup = Mockito.mock(DayGroupImpl.class);
		IdUtil.assignId(dayGroup, dayGroupId);
	
		final String[] columns =getLineFromFile(3);
		final Day<?> day = converter.convert(Pair.of(columns, Pair.of(Map.of(dayGroupId, dayGroup), Map.of(cycleId, cycle))));
			
		assertTrue(day instanceof DayOfWeekDayImpl);
		assertEquals(Integer.parseInt(columns[1]) , ReflectionTestUtils.getField(day, "value"));
		assertEquals(columns[2], day.description().orElseThrow());
		assertEquals(day.dayGroup(), dayGroup);
		
	}
	
	@Test
	void convertLocalDateDay() throws IOException {
		final String[] columns =getLineFromFile(4);
		final Day<?> day = converter.convert(Pair.of(columns, Pair.of(Map.of(), Map.of())));
			
		assertTrue(day instanceof LocalDateDayImp);
		assertEquals(LocalDate.of(1900, 1, 1) , day.value());
		assertTrue(day.description().isEmpty());
		assertEquals(IdUtil.id(Long.parseLong(columns[3])), IdUtil.getId(day.dayGroup()));
		assertEquals(columns[4], day.dayGroup().name());
		assertEquals(Boolean.parseBoolean(columns[5]), day.dayGroup().readOnly());	
		assertEquals(IdUtil.id(Long.parseLong(columns[6])), IdUtil.getId(day.dayGroup().cycle()));
		assertEquals(columns[7], day.dayGroup().cycle().name());
		assertEquals(Integer.parseInt(columns[8]),day.dayGroup().cycle().priority());
		assertEquals(Boolean.parseBoolean(columns[9].strip()),day.dayGroup().cycle().isDeaultCycle());
	}

	

}
