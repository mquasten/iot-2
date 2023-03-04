package de.mq.iot2.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.calendar.DayGroup;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = AbstractDay.class, idClass = String.class)
public interface DayRepository {

	Optional<Day<?>> findById(final String id);

	Collection<Day<?>> findAll();

	@Query("select d from LocaldateDay d")
	Collection<Day<LocalDate>> findAllLocalDateDays();

	@Query("select d from DayOfWeekDay d")
	Collection<Day<DayOfWeek>> findAllDayOfWeekDays();

	Collection<Day<?>> findByDayGroup(final DayGroup dayGroup);

	Day<?> save(@Valid final Day<?> day);

	void delete(final Day<?> entity);

}
