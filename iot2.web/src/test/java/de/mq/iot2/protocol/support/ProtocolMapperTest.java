package de.mq.iot2.protocol.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.i18n.LocaleContext;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.LocaleContextRepository;

class ProtocolMapperTest {
	
	private final  LocaleContextRepository localeContextRepository = Mockito.mock(LocaleContextRepository.class);
	
	private final LocaleContext localeContext = Mockito.mock(LocaleContext.class);
	
	private final ProtocolMapper protocolMapper = new ProtocolMapper(localeContextRepository);
	
	private static final String ID = UUID.randomUUID().toString();
	private static final String NAME = random();
	private static final String LOGMESSAGE = random();
	private static final LocalDateTime DATETIME =  LocalDateTime.now();


	private static String random() {

		return RandomStringUtils.secure().next(50);
	}
	

	
	@Test
	void toWeb() {
		final Protocol protocol = prepareTest(Locale.GERMAN);
		
		final ProtocolModel result= protocolMapper.toWeb(protocol);
		
		assertEquals(ID, result.getId());
		assertEquals(NAME, result.getName());
		assertEquals(LOGMESSAGE, result.getLogMessage());
		assertEquals(DateTimeFormatter.ofPattern(ProtocolMapper.DATE_TIME_FORMAT_GERMAN).format(DATETIME), result.getExecutionTime());
		assertEquals(Protocol.Status.Success, result.getStatus());
		
		
	}
	
	@Test
	void toWebEnglishDateTimeFormat() {
		final ProtocolModel result= protocolMapper.toWeb(prepareTest(Locale.ENGLISH));
		assertEquals(DateTimeFormatter.ofPattern(ProtocolMapper.DATE_TIME_FORMAT_ENGLISH).format(DATETIME), result.getExecutionTime());
	}
	
	@Test
	void toWebExecutionTimeNull() {
		final Protocol protocol = prepareTest(Locale.GERMAN);
		when(protocol.executionTime()).thenReturn(null);
		final ProtocolModel result= protocolMapper.toWeb(protocol);
		assertNull(result.getExecutionTime());
	}




	private Protocol prepareTest(final Locale locale) {
		Mockito.when(localeContextRepository.localeContext()).thenReturn(localeContext);
		when(localeContext.getLocale()).thenReturn(locale);
		
		final Protocol protocol =  Mockito.mock(ProtocolImpl.class);
		
		IdUtil.assignId(protocol, ID);
		
		when(protocol.name()).thenReturn(NAME);
		when(protocol.logMessage()).thenReturn(Optional.of(LOGMESSAGE));
		when(protocol.status()).thenReturn(Protocol.Status.Success);
		when(protocol.executionTime()).thenReturn(DATETIME);
		return protocol;
	}
	

}
