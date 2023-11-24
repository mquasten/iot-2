package de.mq.iot2.protocol.support;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

@Aspect
@Component
class ProtocolServiceAspectImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(ProtocolServiceAspectImpl.class);

	private final Collection<String> batches;

	private final ProtocolService protocolService;

	ProtocolServiceAspectImpl(final ProtocolService protocolService, @Value("${iot2.protocol.batches:}") final String batches) {
		this.protocolService = protocolService;
		this.batches = StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(batches));
	}

	@Around("within(de.mq.iot2.protocol.support.ProtocolServiceImpl) && @annotation(CheckConfiguration)")
	public Object protocolServiceAroundAdvice(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		final Optional<Protocol> protocol = protocol(proceedingJoinPoint);
		if (protocol.isEmpty()) {
			return proceedingJoinPoint.proceed();
		}

		if (batches.contains(protocol.get().name())) {
			LOGGER.debug("Save protocol information to database for {}.", protocol.get().name());
			return proceedingJoinPoint.proceed();
		}

		LOGGER.debug("Protocol informations are disabled for {}.", protocol.get().name());

		return null;

	}

	private Optional<Protocol> protocol(final ProceedingJoinPoint proceedingJoinPoint) {
		return List.of(proceedingJoinPoint.getArgs()).stream().filter(arg -> Protocol.class.isInstance(arg)).map(x -> (Protocol) x).findFirst();
	}

	@Around("within(de.mq.iot2.calendar.support.EndOfDayServiceImpl)||within(de.mq.iot2.calendar.support.CleanupCalendarServiceImpl)||within(de.mq.iot2.protocol.support.CeanupProtocolServiceImp)")
	public Object serviceAroundAdvice(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		try {
			return proceedingJoinPoint.proceed();
		} catch (final Throwable throwable) {
			protocol(proceedingJoinPoint).ifPresent(protocol -> protocolService.error(protocol, throwable));
			throw throwable;
		}
	}
}
