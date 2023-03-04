package de.mq.iot2.sysvars.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot2.sysvars.SystemVariable;
import de.mq.iot2.sysvars.SystemVariableService;

@Service
class SystemVariableServiceImpl implements SystemVariableService {
	private static Logger LOGGER = LoggerFactory.getLogger(SystemVariableServiceImpl.class);
	private final SystemVariableRepository systemVariableRepository;

	SystemVariableServiceImpl(SystemVariableRepository systemVariableRepository) {
		this.systemVariableRepository = systemVariableRepository;
	}

	@Override
	public void update(Collection<SystemVariable> systemVariables) {
		Assert.notNull(systemVariables, "SystemVariables shouldn't be null.");
		final var existingSystemVariables = systemVariableRepository.readSystemVariables().stream().collect(Collectors.toMap(SystemVariable::getName, Function.identity()));
		LOGGER.debug("{} Systemvariables read from ccu2 '{}'.", existingSystemVariables.size(), existingSystemVariables.keySet());
		final Collection<SystemVariable> systemVariables4Update = new ArrayList<>();
		for (final SystemVariable systemVariable : systemVariables) {
			systemVariableNameAndValueRequiredGuard(systemVariable);
			if (!existingSystemVariables.containsKey(systemVariable.getName())) {
				LOGGER.warn("SystemVariable {} doesn't exist at ccu2.");
				continue;
			}

			final SystemVariable existingSystemVariable = existingSystemVariables.get(systemVariable.getName());
			systemVariableNameAndValueRequiredGuard(systemVariable);
			idRequiredGuard(existingSystemVariable);
			if (identical(systemVariable, existingSystemVariable)) {
				continue;
			}

			final SystemVariable systemVariable4Update = new SystemVariable(systemVariable.getName(), systemVariable.getValue());
			systemVariable4Update.setId(existingSystemVariable.getId());
			systemVariables4Update.add(systemVariable4Update);
		}
		LOGGER.debug("{} SystemVariables need update.", systemVariables4Update.size());

		systemVariables4Update.forEach(systemVariableRepository::updateSystemVariable);

	}

	private void idRequiredGuard(final SystemVariable existingSystemVariable) {
		Assert.hasText(existingSystemVariable.getId(), "Id is required.");
	}

	private void systemVariableNameAndValueRequiredGuard(final SystemVariable systemVariable) {
		Assert.hasText(systemVariable.getName(), "Name is required.");
		Assert.notNull(systemVariable.getValue(), "Value is required.");
	}

	private boolean identical(final SystemVariable systemVariable, final SystemVariable existingSystemVariable) {
		return systemVariable.getValue().equals(existingSystemVariable.getValue());
	}

	@Override
	public Collection<SystemVariable> read() {
		return systemVariableRepository.readSystemVariables();
	}

}
