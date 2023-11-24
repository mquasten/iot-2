package de.mq.iot2.protocol.support;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;
import de.mq.iot2.protocol.CeanupProtocolService;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.ProtocolService;

@Service
class CeanupProtocolServiceImp implements CeanupProtocolService {
	static final String RESULT_PROTOCOLS_DELETED = "ProtocolsDeleted";
	static final String NOTHING_REMOVED = "Configuration for cleanup is missing. Nothing will be deleted.";
	private static Logger LOGGER = LoggerFactory.getLogger(CeanupProtocolServiceImp.class);

	private final ConfigurationService configurationService;
	private final ProtocolService protocolService;

	CeanupProtocolServiceImp(final ConfigurationService configurationService, final ProtocolService protocolService) {
		this.configurationService = configurationService;
		this.protocolService = protocolService;
	}

	@Override
	public void execute(final Protocol protocol) {
		protocolService.save(protocol);
		final Optional<Integer> protocolBack = configurationService.parameter(RuleKey.CleanUp, Key.ProtocolBack, Integer.class);

		protocolService.assignParameter(protocol, ProtocolParameterType.Configuration, Key.ProtocolBack.name(), protocolBack);
		if (protocolBack.isEmpty()) {
			LOGGER.warn(NOTHING_REMOVED);
			protocolService.success(protocol, NOTHING_REMOVED);
			return;
		}

		LOGGER.info("Delete protocols elder or equals {} days back.", protocolBack.get());
		final var numberOfProtocolsDeleted = protocolService.deleteProtocols(protocolBack.get());
		LOGGER.info("{} protocols deleted.", numberOfProtocolsDeleted);
		protocolService.assignParameter(protocol, ProtocolParameterType.Result, RESULT_PROTOCOLS_DELETED, numberOfProtocolsDeleted);

		protocolService.success(protocol);
	}

}
