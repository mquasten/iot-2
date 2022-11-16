package de.mq.iot2.sysvars.support;

import java.util.Collection;

public interface HomematicCCU2Repository {
	 Collection<SystemVariable> readSystemVariables();
	 
	 public void updateSystemVariable(final SystemVariable systemVariable);
}
