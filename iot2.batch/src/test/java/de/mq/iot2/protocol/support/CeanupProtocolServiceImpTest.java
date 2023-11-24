package de.mq.iot2.protocol.support;
import static de.mq.iot2.configuration.Configuration.RuleKey.CleanUp;
import static de.mq.iot2.configuration.Parameter.Key.DaysBack;
import static de.mq.iot2.configuration.Parameter.Key.ProtocolBack;
import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Configuration;
import static de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType.Result;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.protocol.CeanupProtocolService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;

class CeanupProtocolServiceImpTest {
	
	private static final String CLEANUP_PROTOCOL_BATCH_NAME = "cleanup-protocol";
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	
	private final ProtocolService protocolService = mock(ProtocolService.class);
	private final CeanupProtocolService cleanupProtocolService = new CeanupProtocolServiceImp(configurationService, protocolService);
	private final Protocol protocol = mock(Protocol.class);

	@Test
	void execute() {
		final int protocolBack = 30;
		final int protocolsDeleted = 5;
		when(protocolService.protocol(CLEANUP_PROTOCOL_BATCH_NAME)).thenReturn(protocol);
		when(configurationService.parameter(RuleKey.CleanUp, ProtocolBack, Integer.class)).thenReturn(Optional.of(protocolBack));
		when(protocolService.deleteProtocols(protocolBack)).thenReturn(protocolsDeleted);

		cleanupProtocolService.execute(protocol);

		verify(protocolService).deleteProtocols(protocolBack);
		verify(protocolService).save(protocol);
		verify(protocolService).assignParameter(protocol, Configuration, ProtocolBack.name(), Optional.of(protocolBack));
		verify(protocolService).assignParameter(protocol, Result, CeanupProtocolServiceImp.RESULT_PROTOCOLS_DELETED, protocolsDeleted);
		verify(protocolService).success(protocol);
	}
	
	@Test
	void executeDaysBackEmpty() {
		Mockito.when(configurationService.parameter(CleanUp, DaysBack, Integer.class)).thenReturn(Optional.empty());
		when(protocolService.protocol(CLEANUP_PROTOCOL_BATCH_NAME)).thenReturn(protocol);
		cleanupProtocolService.execute(protocol);

		verify(protocolService, Mockito.never()).deleteProtocols(anyInt());
		verify(protocolService).save(protocol);
		verify(protocolService, times(1)).assignParameter(protocol, Configuration, ProtocolBack.name(), Optional.empty());
		verify(protocolService).success(protocol, CeanupProtocolServiceImp.NOTHING_REMOVED);

	}
}
