package de.mq.iot2.user.support;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;

@Component
class UserMapper implements   ModelMapper<User, UserDetails> {

	static final String USER_ROLE = "USER";

	@Override
	public final UserDetails toWeb(final User domain) {
		return org.springframework.security.core.userdetails.User.withUsername(domain.name())	
        .password(domain.encodedPasswordAndAlgorithm())
        .roles(USER_ROLE)
        .build();
	}

}
