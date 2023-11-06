package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.mq.iot2.support.RandomTestUtil;

class ProtocolParameterPrimaryKeyImplTest {
	private final String protocol = RandomTestUtil.randomString();

	private final String name = RandomTestUtil.randomString();
	
	 private final ProtocolParameterPrimaryKeyImpl  protocolParameterPrimaryKey = new ProtocolParameterPrimaryKeyImpl(name, protocol);
	 
	 @Test
	 void  name() {
		 assertEquals(name, protocolParameterPrimaryKey.name);
		 assertEquals(protocol, protocolParameterPrimaryKey.protocol);
	 }

}
