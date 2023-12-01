package de.mq.iot2.protocol.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.LocaleContextRepository;
import de.mq.iot2.support.ModelMapper;

@Component
class ProtocolMapper implements ModelMapper<Protocol, ProtocolModel> {

	private static final String DATE_TIME_FORMAT_GERMAN = "dd.MM.yyyy HH:mm:ss";

	private static final String DATE_TIME_FORMAT_ENGLISH = "MM/dd/yyyy HH:mm:ss";

	private final LocaleContextRepository localeContextRepository;

	ProtocolMapper(final LocaleContextRepository localeContextRepository) {
		this.localeContextRepository = localeContextRepository;
	}

	@Override
	public ProtocolModel toWeb(final Protocol protocol) {

		final ProtocolModel protocolModel = new ProtocolModel();
		protocolModel.setId(IdUtil.getId(protocol));
		protocolModel.setName(protocol.name());
		protocolModel.setExecutionTime(formatDateTime(protocol.executionTime()));
		protocolModel.setStatus(protocol.status());
		protocolModel.setLogMessage(protocol.logMessage().orElse(null));
		return protocolModel;
	}

	final String formatDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		if (localeContextRepository.localeContext().getLocale().equals(Locale.GERMAN)) {
			return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_GERMAN).format(localDateTime);
		}
		return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_ENGLISH).format(localDateTime);
	}

}
