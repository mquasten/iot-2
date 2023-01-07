package de.mq.iot2.user.support;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class UserDetailsServiceImpl implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return User.withUsername("mquasten")	
        .password("6d616e667265643031")
        .roles("USER")
        .build();
	}

}
