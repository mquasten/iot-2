package de.mq.iot2.user.support;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;

@Service
class UserServiceImpl implements UserService {
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
		}, () -> userRepository.save(new UserImpl(name, rawPassword, algorithm)));
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

}
