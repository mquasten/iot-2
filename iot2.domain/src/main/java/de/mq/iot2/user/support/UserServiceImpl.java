package de.mq.iot2.user.support;

import java.security.Security;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;
import jakarta.persistence.EntityNotFoundException;

@Service
class UserServiceImpl implements UserService {
	static final String USER_NOT_FOUND_MESSAGE = "User %s not found.";
	private static final String MESSAGE_DIGEST = "MessageDigest";
	private final UserRepository userRepository;
	
	UserServiceImpl(final UserRepository userRepository){
		this.userRepository=userRepository;
	}
	
	@Transactional
	@Override
	public Optional<User> user(final String name) {
		nameRequiredGuard(name);
		return userRepository.findByName(name);
	}

	private void nameRequiredGuard(final String name) {
		Assert.hasText(name, "Name is required.");
	}
	@Override
	@Transactional
	public void update(final String name, final String rawPassword, final Optional<String> algorithm ) {
		nameRequiredGuard(name);
		Assert.hasText(rawPassword, "Password is required.");
		Assert.notNull(algorithm, "Algorithm should not be null.");
		
		userRepository.findByName(name).ifPresentOrElse(user -> { 
			algorithm.ifPresentOrElse(value -> user.assingPassword(rawPassword, value), () -> user.assingPassword(rawPassword));
			userRepository.save(user);
		}, () -> userRepository.save(new UserImpl(name, rawPassword, algorithm)));
	}
	
	@Override
	@Transactional
	public void update(final String name, final Locale language) {
		nameRequiredGuard(name);
		Assert.notNull(language, "Language is required.");
		final User user = userRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, name)));
		user.assignLanguage(language);
		userRepository.save(user);
	}
	@Override
	@Transactional
	public boolean delete(final String name) {
		nameRequiredGuard(name);
		final Optional<User> user = userRepository.findByName(name);
		if(user.isEmpty()) {
			return false;
		}
		
		userRepository.delete(user.get());
		return true;
	}
	
	@Override
	public Collection<String> algorithms() {
		return Security.getAlgorithms(MESSAGE_DIGEST).stream().sorted().collect(Collectors.toList());
	}

}
