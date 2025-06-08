package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import de.mq.iot2.sysvars.SystemVariable;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SystemVariablesConfiguration.class })
class ThymeleafTemplateEngineIntegrationTest {

	@Autowired
	private ISpringTemplateEngine templateEngine;
	
	
	@Test
	void createHtml() {
		
		final var  ctx = new Context(Locale.GERMANY);
		final var  entries = List.of(new AbstractMap.SimpleImmutableEntry<>(new SystemVariable("Name01", "Wert01"), false), new AbstractMap.SimpleImmutableEntry<>(new SystemVariable("Name02", "Wert02"), true));
		ctx.setVariable(UpdateSystemVariablesAspectImpl.VARIABLE_NAME, entries);
		final var  htmlContent = templateEngine.process("systemVariables.html", ctx);
		//System.out.println(htmlContent);
		assertTrue(htmlContent.contains("<td>"));
		entries.forEach(entry ->assertTrue(htmlContent.contains(String.format("<td>%s</td>", entry.getKey().getName()))));
		entries.forEach(entry ->assertTrue(htmlContent.contains(String.format("<td>%s</td>", entry.getKey().getValue()))));
		
		assertTrue(htmlContent.contains("<td>ja</td>"));
		assertTrue(htmlContent.contains("<td>nein</td>"));
	}
	
}
