package de.mq.iot2.rules.support;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;


class SimpleRulesEngineListener  implements RuleListener {
	 static final String MESSAGE_PATTERN = "Rule %s failed.";

	 public final void onFailure(final Rule rule, final Facts facts, final Exception exception) { 
		if( exception == null) {
			return ;
		}
		if (exception.getCause() instanceof RuntimeException) { 
			 throw (RuntimeException) exception.getCause();		
		} else if(exception instanceof RuntimeException)
		{
			throw (RuntimeException) exception;
		} else {
			throw new IllegalStateException(String.format(MESSAGE_PATTERN, rule.getName()),exception);
		}
		
	 }
	 
	
	 public final void onEvaluationError(final Rule rule, final Facts facts, final Exception exception) { 
		 onFailure(rule, facts, exception);
	 }
	   
	  

}
