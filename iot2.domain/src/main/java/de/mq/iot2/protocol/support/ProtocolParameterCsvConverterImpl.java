package de.mq.iot2.protocol.support;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import de.mq.iot2.support.CsvUtil;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.DiscriminatorValue;

@Component
class ProtocolParameterCsvConverterImpl implements Converter<Pair<ProtocolParameter, Boolean>, String[]> {

	private static final int MAX_LENGTH_LOG = 1000;
	private final String csvDelimiter;
	


	ProtocolParameterCsvConverterImpl(@Value("${iot2.csv.delimiter:;}") final String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
	}

	@Override
	public String[] convert(final Pair<ProtocolParameter, Boolean> pair) {
		Assert.notNull(pair, "Value is required.");
		final var parameter = pair.getFirst();
		final var protocolProcessed = pair.getSecond();
		
		

		final Stream<String> results = Stream.concat(Stream.of(parameter.getClass().getAnnotation(DiscriminatorValue.class).value(), CsvUtil.quote(parameter.name(), csvDelimiter), type(parameter), CsvUtil.quote(parameter.value(), csvDelimiter)

		), protocol(parameter.protocol(), protocolProcessed));

		return results.toArray(size -> new String[size]);
	}

	private String  type(final ProtocolParameter parameter) {
		if (parameter instanceof SystemvariableProtocolParameter) {
			
			return "" + ((SystemvariableProtocolParameter) parameter).status();
		} 
		return  ""+parameter.type();
	}
	
	private Stream<String> protocol(final Protocol protocol, boolean processed) {
		
		
		if (processed) {
			return Stream.concat(Stream.of(IdUtil.getId(protocol)), CsvUtil.emptyColumns(4));
		}
		
		 return Stream.of(IdUtil.getId(protocol), CsvUtil.quote(protocol.name(), csvDelimiter), epochMilliSeconds(protocol.executionTime()), ""+ protocol.status() , CsvUtil.quote(StringUtils.truncate(StringUtils.normalizeSpace(CsvUtil.string(protocol.logMessage())), MAX_LENGTH_LOG),csvDelimiter));
	}

	private String epochMilliSeconds(final LocalDateTime executionTime) {
		return ""+ ZonedDateTime.of(executionTime, ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	


}
