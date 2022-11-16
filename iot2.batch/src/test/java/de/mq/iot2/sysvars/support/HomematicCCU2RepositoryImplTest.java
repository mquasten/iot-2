package de.mq.iot2.sysvars.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SystemVariablesConfiguration.class })
public class HomematicCCU2RepositoryImplTest {
	
	@Autowired
	private HomematicCCU2RepositoryImpl homematicCCU2Repository;
	
	
	@Test
	void testit() {
		homematicCCU2Repository.readSystemVariables();
	}

}
