package de.mq.iot2.protocol.support;

import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
class ProtocolServiceAspectImpl {
	
	private final Collection<String> batches;
	
	ProtocolServiceAspectImpl(@Value("${iot2.protocol.batches:}") final String batches){
		this.batches= StringUtils.commaDelimitedListToSet(batches);
	}
	
	
	@Around("within(de.mq.iot2.protocol.support.ProtocolServiceImpl) && @annotation(CheckConfiguration)")
	public Object employeeAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		return proceedingJoinPoint.proceed();
		
	}

}
