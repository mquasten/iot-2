package de.mq.iot2.user.support;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;

@Service
class UserDetailsServiceImpl implements UserDetailsService {

	static final String USER_NOT_FOUND_MESSAGE = "User %s not found.";
	private final UserService userService;
	private final ModelMapper<User, UserDetails> userConterter;

	UserDetailsServiceImpl(UserService userService, final ModelMapper<User, UserDetails> userConterter) {
		this.userService = userService;
		this.userConterter = userConterter;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return userConterter.toWeb(userService.user(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username))));
	}

}
