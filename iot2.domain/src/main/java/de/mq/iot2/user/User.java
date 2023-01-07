package de.mq.iot2.user;

import java.util.Optional;

public interface User {

	void assingPassword(final String rawPassword, final String algorithm);

	void assingPassword(final String rawPassword);

	String name();

	String encodedPasswordAndAlgorithm();

	String encodedPassword();

	Optional<String> algorithm();

}
