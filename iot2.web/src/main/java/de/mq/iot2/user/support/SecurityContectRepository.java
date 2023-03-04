package de.mq.iot2.user.support;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

@Component
interface SecurityContectRepository {
	@Lookup
	SecurityContext securityContext();

}
