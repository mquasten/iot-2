package de.mq.iot2.user.support;

import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.user.User;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = UserImpl.class, idClass = String.class)
public interface UserRepository {

	User save(@Valid final User dayGroup);
	Optional<User> findByName(final String name);
	
	void delete(final User user);

}