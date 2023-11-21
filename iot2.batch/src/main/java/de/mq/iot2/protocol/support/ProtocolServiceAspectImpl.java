package de.mq.iot2.protocol.support;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.iot2.protocol.Protocol;

@Aspect
@Component
class ProtocolServiceAspectImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(ProtocolServiceAspectImpl.class);
	
	private final Collection<String> batches;
	
	ProtocolServiceAspectImpl(@Value("${iot2.protocol.batches:}") final String batches){
		this.batches= StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(batches));
	}
	
	
	@Around("within(de.mq.iot2.protocol.support.ProtocolServiceImpl) && @annotation(CheckConfiguration)")
	public Object protocolServiceAroundAdvice(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		 final Protocol parameter = (Protocol) DataAccessUtils.requiredSingleResult(List.of(proceedingJoinPoint.getArgs()).stream().filter(arg -> Protocol.class.isInstance(arg)).collect(Collectors.toList()));
		
		 if(batches.contains(parameter.name())) {
			 LOGGER.debug("Save protocol information to database for {}.", parameter.name());
			 return proceedingJoinPoint.proceed();
		 }
		 
	     LOGGER.debug("Protocol informations are disabled for {}.", parameter.name());
	    
		return  null;
		
	}

}
