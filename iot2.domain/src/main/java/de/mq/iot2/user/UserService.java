package de.mq.iot2.user;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

	Optional<User> user(String name);

	void update(String name, String rawPassword, Optional<String> algorithm);

	boolean delete(String name);

	Collection<String> algorithms();

}