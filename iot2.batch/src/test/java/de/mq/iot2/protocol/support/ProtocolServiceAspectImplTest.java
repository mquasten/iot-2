package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

class ProtocolServiceAspectImplTest {

	private final static String END_OF_DAY_BATCH = "end-of-day";
	private final static String BATCHES = String.format("%s,end-of-day-update", END_OF_DAY_BATCH);
	private final ProtocolService protocolService = Mockito.mock(ProtocolService.class);
	private final ProtocolServiceAspectImpl protocolServiceAspect = new ProtocolServiceAspectImpl(protocolService, BATCHES);
	private final Object result = Mockito.mock(Object.class);

	private final ProceedingJoinPoint proceedingJoinPoint = Mockito.mock(ProceedingJoinPoint.class);
	private final Protocol protocol = Mockito.mock(Protocol.class);

	@Test
	void protocolServiceAroundAdvice() throws Throwable {

		Mockito.when(protocol.name()).thenReturn(END_OF_DAY_BATCH);
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] { "", protocol });
		Mockito.when(proceedingJoinPoint.proceed()).thenReturn(result);

		assertEquals(result, protocolServiceAspect.protocolServiceAroundAdvice(proceedingJoinPoint));

		Mockito.verify(proceedingJoinPoint).proceed();
	}

	@Test
	void protocolServiceAroundAdviceNoProtocolParameter() throws Throwable {
		Mockito.when(proceedingJoinPoint.proceed()).thenReturn(result);
		when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {});

		assertEquals(result, protocolServiceAspect.protocolServiceAroundAdvice(proceedingJoinPoint));
		verify(proceedingJoinPoint).proceed();
	}

	@Test
	void protocolServiceAroundAdviceNo() throws Throwable {

		Mockito.when(protocol.name()).thenReturn("cleanup");
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] { "", protocol });
		Mockito.when(proceedingJoinPoint.proceed()).thenReturn(result);

		assertNull(protocolServiceAspect.protocolServiceAroundAdvice(proceedingJoinPoint));

		verify(proceedingJoinPoint, Mockito.never()).proceed();
	}

	@Test
	void serviceAroundAdviceException() throws Throwable {
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] { "", protocol });
		final var exception = new IllegalStateException();
		when(proceedingJoinPoint.proceed()).thenThrow(exception);

		assertEquals(exception, assertThrows(IllegalStateException.class, () -> protocolServiceAspect.serviceAroundAdvice(proceedingJoinPoint)));
		verify(protocolService).error(protocol, exception);
	}

	@Test
	void serviceAroundAdviceOk() throws Throwable {
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] { "", protocol });
		final var exception = new IllegalStateException();
		final Object result = mock(Object.class);

		when(proceedingJoinPoint.proceed()).thenReturn(result);

		assertEquals(result, protocolServiceAspect.serviceAroundAdvice(proceedingJoinPoint));
		verify(protocolService, never()).error(protocol, exception);
	}

	@Test
	void serviceAroundAdviceNoProtocolArgument() throws Throwable {
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {});
		final var exception = new IllegalStateException();
		when(proceedingJoinPoint.proceed()).thenThrow(exception);

		assertEquals(exception, assertThrows(IllegalStateException.class, () -> protocolServiceAspect.serviceAroundAdvice(proceedingJoinPoint)));
		verify(protocolService, Mockito.never()).error(protocol, exception);
	}

}
