package de.mq.iot2.sysvars;

import java.util.Collection;

public interface SystemVariableService {

	void update(final Collection<SystemVariable> systemVariables);

	Collection<SystemVariable> read();

}