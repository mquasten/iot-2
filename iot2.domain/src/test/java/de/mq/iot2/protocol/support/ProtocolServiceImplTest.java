package de.mq.iot2.protocol.support;

import static de.mq.iot2.protocol.Protocol.Status.Started;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.support.RandomTestUtil;

class ProtocolServiceImplTest {
	private final ProtocolRepository protocolRepository = mock(ProtocolRepository.class);
	private final ProtocolParameterRepository protocolParameterRepository = mock(ProtocolParameterRepository.class);
	private final ProtocolService protocolService = new ProtocolServiceImpl(protocolRepository,protocolParameterRepository, new DefaultConversionService());

	@Test
	void create() throws InterruptedException {
		final var name = RandomTestUtil.randomString();
		doAnswer(answer -> answer.getArgument(0, Protocol.class)).when(protocolRepository).save(any(Protocol.class));

		final Protocol protocol = protocolService.create(name);

		assertEquals(name, protocol.name());
		assertEquals(Started, protocol.status());
		assertTrue(Duration.between(protocol.executionTime(), LocalDateTime.now()).getSeconds() < 1);

		verify(protocolRepository).save(protocol);

	}

}
