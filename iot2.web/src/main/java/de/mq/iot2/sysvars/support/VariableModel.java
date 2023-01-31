package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.springframework.data.util.Pair;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.CalendarService.TwilightType;

public class VariableModel {

	private final LocalDate today;
	private final TwilightType twilightType;
	

	private final Pair<LocalTime, LocalTime> sunUpDownToday;

	private final Pair<LocalTime, LocalTime> sunUpDownTomorrow;
	
	

	private final Double maxTemperatureToday;

	private final Double maxTemperatureTomorrow;
	
	private  Locale locale = Locale.getDefault();

	public VariableModel(final LocalDate today, final TwilightType twilightType, final Pair<LocalTime, LocalTime> sunUpDownToday, final Pair<LocalTime, LocalTime> sunUpDownTomorrow, final Double maxTemperatureToday, final Double maxTemperatureTomorrow ) {
		this.today = today;
		this.twilightType = twilightType;
		this.sunUpDownToday = sunUpDownToday;
		this.sunUpDownTomorrow = sunUpDownTomorrow;
		this.maxTemperatureToday=maxTemperatureToday;
		this.maxTemperatureTomorrow=maxTemperatureTomorrow;
	}

	public Pair<LocalTime, LocalTime> getSunUpDownTomorrow() {
		return sunUpDownTomorrow;
	}

	public Pair<LocalTime, LocalTime> getSunUpDownToday() {
		return sunUpDownToday;
	}
	
	public String getToday() {
		return dateToString(today);
	}

	private String dateToString(final LocalDate date) {
		return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale));
	}

	public String getTomorrow() {
		return dateToString(today.plusDays(1));
	}
	
	public String getTwilightType() {
		return twilightType.name().toLowerCase();
	}
	
	void setLocale(final Locale locale) {
		Assert.notNull(locale, "Locale is required.");
		this.locale = locale;
	}
	
	public Double getMaxTemperatureToday() {
		return maxTemperatureToday;
	}

	public Double getMaxTemperatureTomorrow() {
		return maxTemperatureTomorrow;
	}
	
	

}
