package de.mq.iot2.sysvars.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot2.sysvars.SystemVariable;
@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SystemVariablesConfiguration.class, HomematicCCU2RepositoryImpl.class })
public class HomematicCCU2RepositoryImplTest {

	@Autowired
	private HomematicCCU2Repository homematicCCU2Repository;
	@Disabled
	@Test
	void readSystemVariables() {
		final var expectedSystemVariables = Set.of("DailyEvents", "LastBatchrun", "Month", "Temperature", "Time",
				"Workingday");
		final Set<String> systemVariables = homematicCCU2Repository.readSystemVariables().stream()
				.map(SystemVariable::getName).collect(Collectors.toSet());

		assertTrue(systemVariables.size() >= expectedSystemVariables.size());
		assertTrue(systemVariables.containsAll(expectedSystemVariables));

	}
	@Disabled
	@Test
	void updateSystemVariable() {
		final var systemVariable = new SystemVariable();
		systemVariable.setId("6572");
		systemVariable.setValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss")));

		homematicCCU2Repository.updateSystemVariable(systemVariable);
	}

}
