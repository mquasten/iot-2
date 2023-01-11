package de.mq.iot2.user.support;

import static de.mq.iot2.user.support.UserDetailsServiceImpl.USER_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;
import de.mq.iot2.user.UserService;

class UserDetailsServiceImplTest {
	private static final String NAME = "jcmaxwell";
	private final UserService userService = mock(UserService.class);
	@SuppressWarnings("unchecked")
	private final ModelMapper<User, UserDetails> userConterter = mock(ModelMapper.class);
	private final User user = mock(User.class);
	private final UserDetails userDetails = mock(UserDetails.class);
	private final UserDetailsService userDetailsService = new UserDetailsServiceImpl(userService, userConterter);

	@Test
	void loadUserByUsername() {
		when(userService.user(NAME)).thenReturn(Optional.of(user));
		when(userConterter.toWeb(user)).thenReturn(userDetails);
		assertEquals(userDetails, userDetailsService.loadUserByUsername(NAME));
	}

	@Test
	void loadUserByUsernameNotFound() {
		when(userService.user(NAME)).thenReturn(Optional.empty());
		assertEquals(String.format(USER_NOT_FOUND_MESSAGE, NAME),assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(NAME)).getMessage());
	}
}
