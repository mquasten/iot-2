package de.mq.iot2.user.support;

import static de.mq.iot2.user.support.UserMapper.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;

class UserMapperTest {
	private static final String PASSWORD_AND_ALGORITHM = "rotE=-dB/dt-MD5";

	private static final String NAME = "jcmaxwell";

	private final ModelMapper<User, UserDetails> userMapper = new UserMapper();

	private final User user = mock(User.class);

	@Test
	 void toWeb() {
		 Mockito.when(user.name()).thenReturn(NAME);
		 when(user.encodedPasswordAndAlgorithm()).thenReturn(PASSWORD_AND_ALGORITHM);
		 
		 final UserDetails userDetails =  userMapper.toWeb(user);
		 
		 assertEquals(NAME, userDetails.getUsername());
		 assertEquals(PASSWORD_AND_ALGORITHM, userDetails.getPassword());
		 assertEquals("ROLE_" +USER_ROLE, userDetails.getAuthorities().iterator().next().getAuthority());
	 }
}
