package de.mq.iot2.protocol;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Protocol {
	public enum Status {
		Started, Success, Error
	}

	String name();

	LocalDateTime executionTime();

	Status status();

	Optional<String> logMessage();

	void assignErrorState();

	void assignSuccessState();

	void assignLogMessage(final String logMessage);

}
