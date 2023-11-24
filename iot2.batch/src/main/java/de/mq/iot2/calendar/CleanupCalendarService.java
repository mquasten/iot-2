package de.mq.iot2.calendar;

import de.mq.iot2.protocol.Protocol;

public interface CleanupCalendarService {

	void execute(final Protocol protocol);

}