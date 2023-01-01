package de.mq.iot2.calendar.support;

import static de.mq.iot2.calendar.support.CalendarController.REDIRECT_CALENDAR_PATTERN;
import static de.mq.iot2.calendar.support.DayController.DAY_MODEL_AND_VIEW_NAME;
import static de.mq.iot2.calendar.support.DayController.DAY_OF_WEEK_LIST;
import static de.mq.iot2.calendar.support.DayController.LOCAL_DATE_MODEL_AND_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

class DayControllerTest {

	private final CalendarService calendarService = mock(CalendarService.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<Day<?>, DayModel> dayMapper = mock(ModelMapper.class);
	private final DayController dayController = new DayController(calendarService, dayMapper);
	private final Model model = new ExtendedModelMap();
	private final DayGroup dayGroup = new DayGroupImpl(mock(Cycle.class), Long.valueOf((long) (Math.random() * 1e12)), UUID.randomUUID().toString());
	private final Day<?> day = new DayOfMonthImpl(dayGroup, MonthDay.of(1, 1));
	private final BindingResult bindingResult = mock(BindingResult.class);

	@Test
	void deleteDay() {
		doReturn(day).when(dayMapper).toDomain(IdUtil.getId(day));
		final DayModel dayModel = new DayModel();
		dayModel.setId(IdUtil.getId(day));

		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, IdUtil.getId(dayGroup)), dayController.deleteDay(dayModel));
		verify(calendarService).deleteDay(day);
	}

	@Test
	void editLocaldates() {
		final var dayGroupModel = newDayGroupModelWithId();
		dayGroupModel.setName(dayGroup.name());

		assertEquals(LOCAL_DATE_MODEL_AND_VIEW_NAME, dayController.editLocaldates(dayGroupModel, model));

		final LocalDateModel localDateModel = (LocalDateModel) model.getAttribute(LOCAL_DATE_MODEL_AND_VIEW_NAME);
		assertEquals(dayGroup.name(), localDateModel.getDayGroupName());
		assertEquals(IdUtil.getId(dayGroup), localDateModel.getDayGroupId());
	}

	private DayGroupModel newDayGroupModelWithId() {
		final var dayGroupModel = new DayGroupModel();
		dayGroupModel.setId(IdUtil.getId(dayGroup));
		return dayGroupModel;
	}

	@ParameterizedTest
	@MethodSource("locales")
	void addDayOfWeek(Locale locale) {
		final var dayGroupModel = newDayGroupModelWithId();
		when(calendarService.unUsedDaysOfWeek()).thenReturn(List.of(DayOfWeek.SUNDAY));

		assertEquals(DAY_MODEL_AND_VIEW_NAME, dayController.addDayOfWeek(dayGroupModel, model, locale));

		final DayModel dayModel = (DayModel) model.getAttribute(DAY_MODEL_AND_VIEW_NAME);
		assertEquals(IdUtil.getId(dayGroup), dayModel.getDayGroupId());
		assertEquals(DayOfWeekDayImpl.class.getName(), dayModel.getType());

		@SuppressWarnings("unchecked")
		final Collection<Entry<String, String>> days = (Collection<Entry<String, String>>) model.getAttribute(DAY_OF_WEEK_LIST);
		assertEquals(1, days.size());
		assertEquals("" + DayOfWeek.SUNDAY.getValue(), days.iterator().next().getKey());
		assertEquals(DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT_STANDALONE, locale), days.iterator().next().getValue());
	}

	static Collection<Locale> locales() {
		return List.of(Locale.GERMAN, Locale.ENGLISH);
	}

	@Test
	void addDayMonth() {
		final var dayGroupModel = newDayGroupModelWithId();

		assertEquals(DAY_MODEL_AND_VIEW_NAME, dayController.addDayMonth(dayGroupModel, model));
		
		final DayModel dayModel = (DayModel) model.getAttribute(DAY_MODEL_AND_VIEW_NAME);
		assertEquals(IdUtil.getId(dayGroup), dayModel.getDayGroupId());
		assertEquals(DayOfMonthImpl.class.getName(), dayModel.getType());

	}

	@Test
	void addLocalDate() {
		final var localDateModel = mock(LocalDateModel.class);
		final var fromDate = LocalDate.of(2023, 1, 1);
		when(localDateModel.getFromDate()).thenReturn(fromDate);
		final var toDate = LocalDate.of(2023, 1, 2);
		when(localDateModel.getToDate()).thenReturn(toDate);
		when(localDateModel.getDayGroupId()).thenReturn(IdUtil.getId(dayGroup));
		when(localDateModel.getDayGroupName()).thenReturn(dayGroup.name());
		when(calendarService.addLocalDateDays(dayGroup.name(), fromDate, toDate)).thenReturn(2);

		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, IdUtil.getId(dayGroup)), dayController.addLocalDate(localDateModel, bindingResult));
		
		verify(calendarService).addLocalDateDays(dayGroup.name(), fromDate, toDate);
	}

	@Test
	void addLocalDateDatesExists() {
		final var localDateModel = mock(LocalDateModel.class);
		final var fromDate = LocalDate.of(2023, 1, 1);
		final var toDate = LocalDate.of(2023, 1, 2);
		when(localDateModel.getFromDate()).thenReturn(fromDate);
		when(localDateModel.getToDate()).thenReturn(toDate);
		when(localDateModel.getDayGroupId()).thenReturn(IdUtil.getId(dayGroup));
		when(localDateModel.getDayGroupName()).thenReturn(dayGroup.name());
		when(calendarService.addLocalDateDays(dayGroup.name(), fromDate, toDate)).thenReturn(1);

		assertEquals(LOCAL_DATE_MODEL_AND_VIEW_NAME, dayController.addLocalDate(localDateModel, bindingResult));
		verify(calendarService).addLocalDateDays(dayGroup.name(), fromDate, toDate);
	}

	@Test
	void addLocalDateBindingErrors() {
		final var localDateModel = mock(LocalDateModel.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		assertEquals(LOCAL_DATE_MODEL_AND_VIEW_NAME, dayController.addLocalDate(localDateModel, bindingResult));
		verify(calendarService, never()).addLocalDateDays(any(), any(), any());
	}

	@Test
	void deleteLocalDate() {
		final var localDateModel = mock(LocalDateModel.class);
		final var fromDate = LocalDate.of(2023, 1, 1);
		final var toDate = LocalDate.of(2023, 1, 2);
		when(localDateModel.getFromDate()).thenReturn(fromDate);
		when(localDateModel.getToDate()).thenReturn(toDate);
		when(localDateModel.getDayGroupId()).thenReturn(IdUtil.getId(dayGroup));
		when(localDateModel.getDayGroupName()).thenReturn(dayGroup.name());

		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, IdUtil.getId(dayGroup)), dayController.deleteLocalDate(localDateModel, bindingResult));

		verify(calendarService).deleteLocalDateDays(dayGroup.name(), fromDate, toDate);
	}

	@Test
	void deleteLocalDateErrors() {
		final var localDateModel = mock(LocalDateModel.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		assertEquals(LOCAL_DATE_MODEL_AND_VIEW_NAME, dayController.deleteLocalDate(localDateModel, bindingResult));

		verify(calendarService, never()).deleteLocalDateDays(any(), any(), any());
	}

	@Test
	void cancelLocalDate() {
		final var localDateModel = mock(LocalDateModel.class);
		when(localDateModel.getDayGroupId()).thenReturn(IdUtil.getId(dayGroup));

		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, IdUtil.getId(dayGroup)), dayController.cancelLocalDate(localDateModel));
	}

	@Test
	void cancelAdd() {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(IdUtil.getId(dayGroup));

		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, IdUtil.getId(dayGroup)), dayController.cancelAdd(dayModel));
	}

	@Test
	void addDay() {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(IdUtil.getId(dayGroup));
		doReturn(day).when(dayMapper).toDomain(dayModel);
		when(calendarService.createDayIfNotExists(day)).thenReturn(true);

		assertEquals(String.format(REDIRECT_CALENDAR_PATTERN, IdUtil.getId(dayGroup)), dayController.addDay(dayModel, bindingResult, model, Locale.GERMAN));

		verify(calendarService).createDayIfNotExists(day);
	}

	@ParameterizedTest
	@MethodSource("locales")
	void addDayDayOfWeekExists(final Locale locale) {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(IdUtil.getId(dayGroup));
		dayModel.setType(DayOfWeekDayImpl.class.getName());
		doReturn(day).when(dayMapper).toDomain(dayModel);
		when(calendarService.unUsedDaysOfWeek()).thenReturn(List.of(DayOfWeek.SUNDAY));
		when(calendarService.createDayIfNotExists(day)).thenReturn(false);

		assertEquals(DAY_MODEL_AND_VIEW_NAME, dayController.addDay(dayModel, bindingResult, model, locale));

		@SuppressWarnings("unchecked")
		final Collection<Entry<?, ?>> days = (Collection<Entry<?, ?>>) model.getAttribute(DAY_OF_WEEK_LIST);
		assertEquals(1, days.size());
		assertEquals("" + DayOfWeek.SUNDAY.getValue(), days.iterator().next().getKey());
		assertEquals(DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT_STANDALONE, locale), days.iterator().next().getValue());

		verify(calendarService).unUsedDaysOfWeek();
		
		final ArgumentCaptor<ObjectError> errorCaptor = ArgumentCaptor.forClass(ObjectError.class);
		verify(bindingResult).addError(errorCaptor.capture());
		assertEquals(DAY_MODEL_AND_VIEW_NAME, errorCaptor.getValue().getObjectName());
		assertEquals(1, errorCaptor.getValue().getCodes().length);
		assertEquals(DayController.MESSAGE_KEY_DAY_EXISTS, errorCaptor.getValue().getCodes()[0]);
		assertNull(errorCaptor.getValue().getArguments());
		assertEquals("{" +DayController.MESSAGE_KEY_DAY_EXISTS+"}", errorCaptor.getValue().getDefaultMessage());
	}

	@Test
	void addDayDayOfMonthExists() {
		final DayModel dayModel = new DayModel();
		dayModel.setDayGroupId(IdUtil.getId(dayGroup));
		dayModel.setType(DayOfMonthImpl.class.getName());
		doReturn(day).when(dayMapper).toDomain(dayModel);
		when(calendarService.createDayIfNotExists(day)).thenReturn(false);

		assertEquals(DAY_MODEL_AND_VIEW_NAME, dayController.addDay(dayModel, bindingResult, model, Locale.GERMAN));

		assertNull(model.getAttribute(DAY_OF_WEEK_LIST));

		verify(calendarService, never()).unUsedDaysOfWeek();
		
		final ArgumentCaptor<ObjectError> errorCaptor = ArgumentCaptor.forClass(ObjectError.class);
		verify(bindingResult).addError(errorCaptor.capture());
		assertEquals(DAY_MODEL_AND_VIEW_NAME, errorCaptor.getValue().getObjectName());
		assertEquals(1, errorCaptor.getValue().getCodes().length);
		assertEquals(DayController.MESSAGE_KEY_DAY_EXISTS, errorCaptor.getValue().getCodes()[0]);
		assertNull(errorCaptor.getValue().getArguments());
		assertEquals("{" +DayController.MESSAGE_KEY_DAY_EXISTS+"}", errorCaptor.getValue().getDefaultMessage());
	}

	@Test
	void addDayBindingErrors() {
		final DayModel dayModel = new DayModel();
		dayModel.setType(DayOfMonthImpl.class.getName());
		dayModel.setDayGroupId(IdUtil.getId(dayGroup));
		when(bindingResult.hasErrors()).thenReturn(true);

		assertEquals(DAY_MODEL_AND_VIEW_NAME, dayController.addDay(dayModel, bindingResult, model, Locale.GERMAN));

		assertNull(model.getAttribute(DAY_OF_WEEK_LIST));

		verify(calendarService, never()).createDayIfNotExists(day);
		verify(calendarService, never()).unUsedDaysOfWeek();
	}

	@Test
	void addDayBindingErrorsDayOfWeek() {
		final DayModel dayModel = new DayModel();
		dayModel.setType(DayOfWeekDayImpl.class.getName());
		dayModel.setDayGroupId(IdUtil.getId(dayGroup));
		when(calendarService.unUsedDaysOfWeek()).thenReturn(List.of(DayOfWeek.SUNDAY));
		when(bindingResult.hasErrors()).thenReturn(true);

		assertEquals(DAY_MODEL_AND_VIEW_NAME, dayController.addDay(dayModel, bindingResult, model, Locale.GERMAN));

		@SuppressWarnings("unchecked")
		final Collection<Entry<?, ?>> days = (Collection<Entry<?, ?>>) model.getAttribute(DAY_OF_WEEK_LIST);
		assertEquals(1, days.size());
		assertEquals("" + DayOfWeek.SUNDAY.getValue(), days.iterator().next().getKey());
		assertEquals(DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.GERMAN), days.iterator().next().getValue());

		verify(calendarService, never()).createDayIfNotExists(day);
		verify(calendarService).unUsedDaysOfWeek();
	}

}
