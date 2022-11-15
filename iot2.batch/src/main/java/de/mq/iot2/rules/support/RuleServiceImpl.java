package de.mq.iot2.rules.support;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.util.ReflectionUtils;

import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.rules.RuleService;

class RuleServiceImpl implements RuleService {

	private final Collection<? extends Object> pojoRules;

	RuleServiceImpl(final Collection<? extends Object> pojoRules) {
		this.pojoRules = pojoRules;
	}

	@Override
	public Facts process(final Map<Key, ? extends Object> parameter, final Map<? extends Enum<?>, Object> arguments) {
		final RulesEngine rulesEngine = rulesEngine();
		final Rules rules = rules(parameter, pojoRules);
		Facts facts = facts(arguments);
		rulesEngine.fire(rules, facts);
		return facts;
	}

	private  RulesEngine rulesEngine() {
		final DefaultRulesEngine rulesEngine = new DefaultRulesEngine(
				new RulesEngineParameters(false, true, false, Integer.MAX_VALUE));
		rulesEngine.registerRuleListener(new SimpleRulesEngineListener());
		return rulesEngine;
	}

	private Rules rules(final Map<Key, ? extends Object> parameter, final Collection<?> pojoRules) {
		final Rules rules = new Rules();
		for (final Object rule : pojoRules) {
			ReflectionUtils.doWithFields(rule.getClass(), field -> setField(rule, field, parameter),
					field -> field.isAnnotationPresent(ParameterValue.class));
			rules.register(rule);
		}

		return rules;
	}

	private void setField(final Object rule, Field field, final Map<Key, ? extends Object> parameter)
			throws IllegalAccessException {
		field.setAccessible(true);
		final Key key = field.getDeclaredAnnotation(ParameterValue.class).value();
		if (parameter.containsKey(key)) {
			field.set(rule, parameter.get(key));
		}
	}

	private Facts facts(final Map<? extends Enum<?>, ? extends Object> arguments) {
		final Facts facts = new Facts();
		arguments.entrySet().forEach(entry -> facts.put(entry.getKey().name(), entry.getValue()));

		return facts;
	}

}
