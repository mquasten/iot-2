package de.mq.iot2.sysvars.support;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
		final var existingSystemVariables = systemVariableRepository.readSystemVariables();
		LOGGER.debug("{} Systemvariables read from ccu2.", existingSystemVariables.size());
	}

}
