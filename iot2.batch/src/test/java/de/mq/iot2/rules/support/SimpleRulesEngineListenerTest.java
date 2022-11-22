package de.mq.iot2.rules.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SimpleRulesEngineListenerTest {
	
	private final RuleListener ruleListener = new SimpleRulesEngineListener();
	private final Rule rule = Mockito.mock(Rule.class);
	private final Facts facts = new Facts();
	@Test
	void onFailure() {
		final var exception = new IllegalStateException();
		assertEquals( exception,assertThrows(IllegalStateException.class, () -> ruleListener.onEvaluationError(rule, facts, exception)));
	}
	
	@Test
	void onFailureCauseIsRuntimeException() {
		final var cause = new IllegalStateException();
		final var exception = new Exception(cause);
		assertEquals( cause,assertThrows(IllegalStateException.class, () -> ruleListener.onEvaluationError(rule, facts, exception)));
	}
	 
	@Test
	void onFailureCheckedException() {
		Mockito.when(rule.getName()).thenReturn("TestRule");
		final var exception = new Exception("Message");
		
		final var  runtimeException=  assertThrows(IllegalStateException.class, () -> ruleListener.onEvaluationError(rule, facts, exception));
		
		assertTrue( runtimeException instanceof IllegalStateException);
		assertEquals(String.format(SimpleRulesEngineListener.MESSAGE_PATTERN, rule.getName()), runtimeException.getMessage());
		assertEquals(exception, runtimeException.getCause());
			
	}
	
	@Test
	void onFailureExceptionNull() {
		ruleListener.onEvaluationError(rule, facts, null);
	}

}
