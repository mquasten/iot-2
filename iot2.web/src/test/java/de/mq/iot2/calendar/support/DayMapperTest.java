package de.mq.iot2.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.LocaleContextRepository;
import jakarta.persistence.EntityNotFoundException;

class DayMapperTest {
	private static final String VALUE_SORTED_FIELD_NAME = "valueSorted";

	private final LocaleContextRepository localeContextRepository = mock(LocaleContextRepository.class);

	private final DayRepository dayRepository = mock(DayRepository.class);
	private final DayGroupRepository dayGroupRepository = mock(DayGroupRepository.class);

	private final DayMapper dayMapper = new DayMapper(dayGroupRepository, dayRepository, localeContextRepository);

	private final DayGroup dayGroup = mock(DayGroup.class);

	@BeforeEach
	void setup() {
		final LocaleContext localeContext = mock(LocaleContext.class);
		when(localeContext.getLocale()).thenReturn(Locale.GERMAN);
		when(localeContextRepository.localeContext()).thenReturn(localeContext);
	}

	@Test
	void toWeb() {
		final var date = LocalDate.of(1968, 5, 28);
		final Day<?> day = new LocalDateDayImp(dayGroup, date, randomString());
		final var dayModel = dayMapper.toWeb(day);

		assertEquals(IdUtil.getId(day), dayModel.getId());
		assertEquals(day.description().get(), dayModel.getDescription());
		assertEquals(date.format(DateTimeFormatter.ofPattern("dd.MM.yy")), dayModel.getValue());
		assertEquals(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")), ReflectionTestUtils.getField(dayModel, VALUE_SORTED_FIELD_NAME));
	}

	@Test
	void toWebDayOfMonth() {
		final var monthDay = MonthDay.of(5, 28);
		final Day<?> day = new DayOfMonthImpl(dayGroup, monthDay);

		final var dayModel = dayMapper.toWeb(day);

		assertEquals(IdUtil.getId(day), dayModel.getId());
		assertEquals(dayMonthAsDate(monthDay).format(DateTimeFormatter.ofPattern("dd.MM")), dayModel.getValue());
		assertEquals(dayMonthAsDate(monthDay).format(DateTimeFormatter.ofPattern("yyyyMMdd")), ReflectionTestUtils.getField(dayModel, VALUE_SORTED_FIELD_NAME));
	}

	private LocalDate dayMonthAsDate(final MonthDay monthDay) {
		return LocalDate.of(Year.now().getValue(), monthDay.getMonthValue(), monthDay.getDayOfMonth());
	}

	@Test
	void toWebDayOfWeek() {
		final Day<?> day = new DayOfWeekDayImpl(dayGroup, DayOfWeek.SUNDAY);

		final var dayModel = dayMapper.toWeb(day);

		assertEquals(IdUtil.getId(day), dayModel.getId());
		assertEquals(DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.GERMAN), dayModel.getValue());
		assertEquals(DayOfWeek.SUNDAY.name(), ReflectionTestUtils.getField(dayModel, VALUE_SORTED_FIELD_NAME));
	}

	@Test
	void toWebOther() {
		@SuppressWarnings("unchecked")
		final Day<Integer> day = mock(AbstractDay.class);
		IdUtil.assignId(day, randomString());
		final int value = 4711;
		
		when(day.value()).thenReturn(value);

		final var dayModel = dayMapper.toWeb(day);
		assertEquals(String.valueOf(value), dayModel.getValue());
		assertEquals(String.valueOf(value), ReflectionTestUtils.getField(dayModel, VALUE_SORTED_FIELD_NAME));
	}
	
	@Test
	void toDomainFromId() {
		final var id = randomString();
		final Day<?> day = mock(Day.class);
		when(dayRepository.findById(id)).thenReturn(Optional.of(day));
		
		assertEquals(day, dayMapper.toDomain(id));
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}
	
	@Test
	void toDomainFromIdNotFound() {
		final var id = randomString();
		when(dayRepository.findById(id)).thenReturn(Optional.empty());
		
		assertEquals(String.format(DayMapper.DAY_NOT_FOUND_MESSAGE, id), assertThrows(EntityNotFoundException.class, ()-> dayMapper.toDomain(id)).getMessage());
	}
	
	@Test
	void toDomain() {
		final var dayGroupId = randomString();
		final var value = MonthDay.of(5, 28);
		final var description = randomString();
		final var dayModel = mock(DayModel.class);
		when(dayModel.getDayGroupId()).thenReturn(dayGroupId);
		when(dayModel.getType()).thenReturn(DayOfMonthImpl.class.getName());
		when(dayModel.getDescription()).thenReturn(description);
		when(dayModel.getTargetValue()).thenReturn(value);
		doReturn(DayOfMonthImpl.class).when(dayModel).targetEntity();
		when(dayGroupRepository.findById(dayGroupId)).thenReturn(Optional.of(dayGroup));
		
		final Day<?> day = dayMapper.toDomain(dayModel);
		
		assertEquals(dayGroup, day.dayGroup());
		assertEquals(value, day.value());
		assertEquals(Optional.of(description), day.description());
	}
	
	@Test
	void toDomainDayGroupNotFound() {
		final var dayGroupId = randomString();
		final var dayModel = mock(DayModel.class);
		when(dayModel.getDayGroupId()).thenReturn(dayGroupId);
		when(dayModel.getType()).thenReturn(DayOfMonthImpl.class.getName());
		when(dayGroupRepository.findById(dayGroupId)).thenReturn(Optional.empty());
		
		assertEquals(String.format(CalendarServiceImp.DAY_GROUP_NOT_FOUND_MESSAGE, dayGroupId), assertThrows(EntityNotFoundException.class, ()->  dayMapper.toDomain(dayModel)).getMessage());
	}
	
	@Test
	void toDomainCanNotCreateEntity() {
		final var dayGroupId = randomString();
		final var value = MonthDay.of(5, 28);
		final var description = randomString();
		final var dayModel = mock(DayModel.class);
		when(dayModel.getDayGroupId()).thenReturn(dayGroupId);
		when(dayModel.getType()).thenReturn(DayOfMonthImpl.class.getName());
		when(dayModel.getDescription()).thenReturn(description);
		when(dayModel.getTargetValue()).thenReturn(value);
		doReturn(LocalDate.class).when(dayModel).targetEntity();
		when(dayGroupRepository.findById(dayGroupId)).thenReturn(Optional.of(dayGroup));
		
		assertThrows(IllegalStateException.class, () -> dayMapper.toDomain(dayModel));
	}

}
