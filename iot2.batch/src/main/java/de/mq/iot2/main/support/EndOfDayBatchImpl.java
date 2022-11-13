package de.mq.iot2.main.support;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.calendar.CalendarService.TimeType;
import de.mq.iot2.calendar.CalendarService.TwilightType;
import de.mq.iot2.configuration.Configuration.RuleKey;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.configuration.Parameter.Key;

@Service
public class EndOfDayBatchImpl {

	private final CalendarService calendarService;

	private final ConfigurationService configurationService;

	EndOfDayBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
	}

	@BatchMethod(value = "end-of-day", converterClass = EndOfDayBatchArgumentConverterImpl.class)
	final void execute(final LocalDate date) {

		System.out.println("Use date:" + date);

		final var cycle = calendarService.cycle(date);

		System.out.println("Cycle:" + cycle.name());

		final var parameters = configurationService.parameters(RuleKey.EndOfDay, cycle);

		parameters.entrySet().forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));

		final TimeType timeType = calendarService.timeType(date);

		System.out.println("TimeType:" + timeType);
		final var twilightType = parameters.containsKey(Key.SunUpDownType) ? (TwilightType) parameters.get(Key.SunUpDownType) : TwilightType.Mathematical;

		System.out.println("TwilightType:" + twilightType);

		final var sunUpTime = calendarService.sunUpTime(date, twilightType);
		System.out.println("SunUpTime:" + sunUpTime);

		final var sunDownTime = calendarService.sunDownTime(date, twilightType);
		System.out.println("SunDownTime:" + sunDownTime);

		final var facts = Map.of("parameters", parameters, "timeType", timeType, "sunUpTime", sunUpTime, "sunDownTime", sunDownTime);
		System.out.println(facts);

	}

}
