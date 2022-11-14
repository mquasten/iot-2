package de.mq.iot2.rules.support;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;

class SimpleRulesEngineListener  implements RuleListener {
	 public void onFailure(Rule rule, Facts facts, Exception exception) { 
		 
		if (exception.getCause() instanceof RuntimeException) { 
			 throw (RuntimeException) exception.getCause();
			
		} else if(exception instanceof RuntimeException)
		{
			throw (RuntimeException) exception;
		} else {
			throw new IllegalStateException(String.format("Rule %s failed.", rule.getName()),exception);
		}
		
	 }

}
