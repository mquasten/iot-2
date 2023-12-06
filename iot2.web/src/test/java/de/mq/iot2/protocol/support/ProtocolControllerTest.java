package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolService;
import de.mq.iot2.support.ModelMapper;

class ProtocolControllerTest {

	private static final String ID = UUID.randomUUID().toString();
	private static final String END_OF_DAY_BATCH = "end-of-day";
	private static final List<String> BATCHES = List.of(END_OF_DAY_BATCH, "end-of-day-update", "cleanup-calendar", "cleanup-protocol");
	private final ProtocolService protocolService = mock(ProtocolService.class);
	private final ModelMapper<Protocol, ProtocolModel> protocolMapper = mock(ProtocolMapper.class);

	private final ProtocolController protocolController = new ProtocolController(protocolService, protocolMapper);

	private final Model model = new ExtendedModelMap();

	@Test
	void protocol() {
		Collection<Protocol> protocols = List.of(mock(Protocol.class), mock(Protocol.class));
		Collection<ProtocolModel> protocolModels = List.of(mock(ProtocolModel.class), mock(ProtocolModel.class));
		when(protocolService.protocolNames()).thenReturn(BATCHES);
		when(protocolService.protocols(END_OF_DAY_BATCH)).thenReturn(protocols);
		when(protocolMapper.toWeb(protocols)).thenReturn(protocolModels);

		assertEquals(ProtocolController.PROTOCOL_MODEL_AND_VIEW_NAME, protocolController.protocol(model, END_OF_DAY_BATCH));

		assertEquals(BATCHES, model.getAttribute(ProtocolController.BATCHES_ATTRIBUTE_NAME));
		final ProtocolModel result = (ProtocolModel) model.getAttribute(ProtocolController.PROTOCOL_MODEL_AND_VIEW_NAME);
		assertEquals(END_OF_DAY_BATCH, result.getName());
		assertEquals(protocolModels, result.getProtocols());
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { "", " ", "\t" })
	void protocolBatchNameMissing(final String batchName) {

		Mockito.when(protocolService.protocolNames()).thenReturn(BATCHES);

		assertEquals(ProtocolController.PROTOCOL_MODEL_AND_VIEW_NAME, protocolController.protocol(model, batchName));

		assertEquals(BATCHES, model.getAttribute(ProtocolController.BATCHES_ATTRIBUTE_NAME));
		final ProtocolModel result = (ProtocolModel) model.getAttribute(ProtocolController.PROTOCOL_MODEL_AND_VIEW_NAME);
		assertNull(result.getName());

		assertTrue(CollectionUtils.isEmpty(result.getProtocols()));
		verify(protocolService, never()).protocols(any());
		verify(protocolMapper, never()).toWeb(anyCollection());
	}

	@Test
	void search() {
		final var protocolModel = mock(ProtocolModel.class);
		when(protocolModel.getName()).thenReturn(END_OF_DAY_BATCH);

		assertEquals(String.format(ProtocolController.REDIRECT_PROTOCOL_PATTERN, END_OF_DAY_BATCH), protocolController.search(protocolModel, mock(BindingResult.class)));
	}

	@Test
	void showLog() {
		final ProtocolModel protocolModel = mock(ProtocolModel.class);
		final ProtocolModel protocolWeb = mock(ProtocolModel.class);
		final Protocol protocol = mock(Protocol.class);
		when(protocolModel.getId()).thenReturn(ID);
		when(protocolService.protocolById(ID)).thenReturn(protocol);
		when(protocolMapper.toWeb(protocol)).thenReturn(protocolWeb);

		assertEquals(ProtocolController.LOGMESSAGE_VIEW, protocolController.showLog(protocolModel, model));

		assertEquals(protocolWeb, model.getAttribute(ProtocolController.PROTOCOL_MODEL_AND_VIEW_NAME));
		verify(protocolService).protocolById(ID);
		verify(protocolMapper).toWeb(protocol);
	}

	@Test
	void chancelProtocol() {
		final ProtocolModel protocolModel = mock(ProtocolModel.class);
		when(protocolModel.getName()).thenReturn(END_OF_DAY_BATCH);

		assertEquals(String.format(ProtocolController.REDIRECT_PROTOCOL_PATTERN, END_OF_DAY_BATCH), protocolController.chancelProtocol(protocolModel, model));
	}
}
