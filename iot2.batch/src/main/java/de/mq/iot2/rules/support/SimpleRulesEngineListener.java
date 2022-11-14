package de.mq.iot2.rules.support;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;

class SimpleRulesEngineListener  implements RuleListener {
	 final public void onFailure(final Rule rule, final Facts facts, final Exception exception) { 
		if (exception.getCause() instanceof RuntimeException) { 
			 throw (RuntimeException) exception.getCause();		
		} else if(exception instanceof RuntimeException)
		{
			throw (RuntimeException) exception;
		} else {
			throw new IllegalStateException(String.format("Rule %s failed.", rule.getName()),exception);
		}
		
	 }
	 
	 public void onEvaluationError(final Rule rule, final Facts facts, final Exception exception) { 
		 onFailure(rule, facts, exception);
	 }

}
