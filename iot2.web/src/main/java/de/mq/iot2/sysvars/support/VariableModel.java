package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.util.Pair;

import de.mq.iot2.calendar.CalendarService.TwilightType;

public class VariableModel {

	private final LocalDate today;
	private final TwilightType twilightType;
	

	private final Pair<LocalTime, LocalTime> sunUpDownToday;

	private final Pair<LocalTime, LocalTime> sunUpDownTomorrow;

	public VariableModel(final LocalDate today, final TwilightType twilightType, final Pair<LocalTime, LocalTime> sunUpDownToday,
			final Pair<LocalTime, LocalTime> sunUpDownTomorrow) {
		this.today = today;
		this.twilightType = twilightType;
		this.sunUpDownToday = sunUpDownToday;
		this.sunUpDownTomorrow = sunUpDownTomorrow;
	}

	public Pair<LocalTime, LocalTime> getSunUpDownTomorrow() {
		return sunUpDownTomorrow;
	}

	public Pair<LocalTime, LocalTime> getSunUpDownToday() {
		return sunUpDownToday;
	}

	public LocalDate getToday() {
		return today;
	}

	public LocalDate getTomorrow() {
		return today.plusDays(1);
	}
	
	public TwilightType getTwilightType() {
		return twilightType;
	}

}
