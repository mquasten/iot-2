package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.protocol.Protocol;

class ProtocolServiceAspectImplTest {
	
	private final static String END_OF_DAY_BATCH = "end-of-day";
	private final static String BATCHES =String.format("%s,end-of-day-update", END_OF_DAY_BATCH);
	private final ProtocolServiceAspectImpl protocolServiceAspect = new ProtocolServiceAspectImpl(BATCHES);
	private final Object result= Mockito.mock(Object.class);
	
	private final ProceedingJoinPoint proceedingJoinPoint = Mockito.mock(ProceedingJoinPoint.class);
	private final Protocol protocol = Mockito.mock(Protocol.class);
	
	@Test
	void protocolServiceAroundAdvice() throws Throwable {
	
		Mockito.when(protocol.name()).thenReturn(END_OF_DAY_BATCH);
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {"" , protocol});
		Mockito.when(proceedingJoinPoint.proceed()).thenReturn(result);
	
		assertEquals(result, protocolServiceAspect.protocolServiceAroundAdvice(proceedingJoinPoint));
		
		Mockito.verify(proceedingJoinPoint).proceed();
	}
	
	@Test
	void protocolServiceAroundAdviceNo() throws Throwable {
	
		Mockito.when(protocol.name()).thenReturn("cleanup");
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {"" , protocol});
		Mockito.when(proceedingJoinPoint.proceed()).thenReturn(result);
	
		assertNull(protocolServiceAspect.protocolServiceAroundAdvice(proceedingJoinPoint));
		
		Mockito.verify(proceedingJoinPoint, Mockito.never()).proceed();
	}

}
