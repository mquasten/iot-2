package de.mq.iot2.protocol.support;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.ProtocolParameter.ProtocolParameterType;
import de.mq.iot2.protocol.SystemvariableProtocolParameter.SystemvariableStatus;

class Array2ProtocolParameterConverterImpl implements Converter<Pair<String[], Map<String, Protocol>>, ProtocolParameter> {

	static final String WRONG_NUMBER_OF_COLUMNS_MESSAGE = "9 columns expected.";

	private final ConversionService conversionService;

	Array2ProtocolParameterConverterImpl(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	Map<String, BiFunction<String[], Map<String, Protocol>, ProtocolParameter>> pararemtes = Map.of(ProtocolParameterImpl.DISCRIMINATOR_VALUE, this::parameter, SystemvariableProtocolParameterImpl.DISCRIMINATOR_VALUE, this::systemParameter);

	@Override
	public ProtocolParameter convert(final Pair<String[], Map<String, Protocol>> data) {
		Assert.notNull(data, "Input is required.");
		final String[] columns = data.getFirst();
		final Map<String, Protocol> protocols = data.getSecond();

		Assert.isTrue(columns.length == 9, WRONG_NUMBER_OF_COLUMNS_MESSAGE);

		final String type = value(columns, 0);

		final BiFunction<String[], Map<String, Protocol>, ProtocolParameter> function = pararemtes.get(type);
		Assert.notNull(function, String.format("ParameterType %s unkown.", type));
		return function.apply(columns, protocols);
	}

	private ProtocolParameter parameter(final String[] columns, final Map<String, Protocol> protocols) {
		final var protocol = protocol(columns, protocols);
		final var name = value(columns, 1);
		final var type = ProtocolParameterType.valueOf(value(columns, 2));
		final var value = value(columns, 3);
		return new ProtocolParameterImpl(protocol, name, type, value);

	}

	private Protocol protocol(final String[] columns, final Map<String, Protocol> protocols) {
		final var protocolId = value(columns, 4);

		if (protocols.containsKey(protocolId)) {
			return protocols.get(protocolId);
		}

		final var protocolName = value(columns, 5);
		final var executionTimeAsLong = value(columns, 6, Long.class);
		final var executionTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(executionTimeAsLong), ZoneId.systemDefault());
		final var status = Protocol.Status.valueOf(value(columns, 7));
		final var logMessage = optionalValue(columns, 8);
		return new ProtocolImpl(protocolId, protocolName, executionTime, status, logMessage);
	}

	private ProtocolParameter systemParameter(final String[] columns, final Map<String, Protocol> protocols) {

		final var protocol = protocol(columns, protocols);
		final var name = value(columns, 1);
		final var status = SystemvariableStatus.valueOf(value(columns, 2));
		final var value = value(columns, 3);
		return new SystemvariableProtocolParameterImpl(protocol, name, value, status);
	}

	private String value(final String[] columns, final int col) {
		final String result = columns[col].strip();
		Assert.hasText(result, String.format("Value reuired in Column %s", col));
		return result;
	}

	private String optionalValue(final String[] columns, final int col) {
		final String result = columns[col].strip();
		if (StringUtils.hasText(result)) {
			return result;
		}
		return null;
	}

	private <T> T value(final String[] columns, final int col, Class<T> type) {
		return value(value(columns, col), type);
	}

	private <T> T value(final String value, final Class<T> type) {
		return conversionService.convert(value, type);
	}

}
