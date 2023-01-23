package de.mq.iot2.user;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public interface UserService {

	Optional<User> user(final String name);

	void update(final String name, final String rawPassword, final Optional<String> algorithm);

	boolean delete(final String name);

	Collection<String> algorithms();

	void update(final String name, final Locale language);

}