package de.mq.iot2.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import de.mq.iot2.protocol.Protocol;

public interface EndOfDayService {

	void execute(final Protocol protocol, final LocalDate date, final Optional<LocalTime> uptateTime);

}