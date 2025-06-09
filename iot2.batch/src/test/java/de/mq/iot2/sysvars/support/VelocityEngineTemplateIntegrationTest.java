package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.spring.VelocityEngineUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot2.sysvars.SystemVariable;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SystemVariablesConfiguration.class })
class VelocityEngineTemplateIntegrationTest {

	@Autowired
	private VelocityEngine velocityEngine;

	@Test
	void createHtml() {

		final var entries = List.of(new AbstractMap.SimpleImmutableEntry<>(new SystemVariable("Name01", "Wert01"), false), new AbstractMap.SimpleImmutableEntry<>(new SystemVariable("Name02", "Wert02"), true));

		final Map<String, Object> model = new HashMap<>();
		model.put(UpdateSystemVariablesAspectImpl.VARIABLE_NAME, entries);
		final String htmlContent = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "email-templates/systemvariables.vm", "UTF-8", model);

		// System.out.println(htmlContent);
		assertTrue(htmlContent.contains("<td>"));
		entries.forEach(entry -> assertTrue(htmlContent.contains(String.format("<td>%s</td>", entry.getKey().getName()))));
		entries.forEach(entry -> assertTrue(htmlContent.contains(String.format("<td>%s</td>", entry.getKey().getValue()))));

		assertTrue(htmlContent.contains("<td>ja</td>"));
		assertTrue(htmlContent.contains("<td>nein</td>"));
	}

}
