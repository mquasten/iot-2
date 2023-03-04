package de.mq.iot2.rules.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RulesConfigurationTest {

	private final RulesConfiguration rulesConfiguration = new RulesConfiguration();

	@Test
	void endOfDayRulesService() {
		final var ruleService = rulesConfiguration.endOfDayRuleService();
		final Collection<?> rulesClasses = ((Collection<?>) ReflectionTestUtils.getField(ruleService, "pojoRules")).stream().map(Object::getClass).collect(Collectors.toList());

		assertEquals(2, rulesClasses.size());
		assertTrue(rulesClasses.containsAll(List.of(TimerRuleImpl.class, OtherVariablesRulesImpl.class)));
	}

}
