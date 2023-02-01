package de.mq.iot2.sysvars.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.springframework.util.Assert;

public class VariableModel {

	private final LocalDate today = LocalDate.now();
	private String  twilightType;
	private  String  sunUpToday;
	private  String  sunDownToday;
	private  String  sunUpTomorrow;
	private  String  sunDownTomorrow;

	private  String maxTemperatureToday;

	private  String maxTemperatureTomorrow;
	
	private  Locale locale = Locale.getDefault();

	public VariableModel() {
	
	}
	 public final LocalDate getDate() {
		 dateExistsGuard();
		 return today;
	 }
	
	public String getToday() {
		dateExistsGuard();
		return dateToString(today);
	}

	private void dateExistsGuard() {
		Assert.notNull(today, "Date required.");
	}

	private String dateToString(final LocalDate date) {
		return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale));
	}

	public String getTomorrow() {
		dateExistsGuard();
		return dateToString(today.plusDays(1));
	}
	
	void setLocale(final Locale locale) {
		Assert.notNull(locale, "Locale is required.");
		this.locale = locale;
	}
	
	public String getTwilightType() {
		return twilightType;
	}

	public void setTwilightType(final String twilightType) {
		this.twilightType = twilightType;
	}

	public String getSunUpToday() {
		return sunUpToday;
	}

	public void setSunUpToday(final String sunUpToday) {
		this.sunUpToday = sunUpToday;
	}

	public String getSunDownToday() {
		return sunDownToday;
	}

	public void setSunDownToday(final String sunDownToday) {
		this.sunDownToday = sunDownToday;
	}

	public String getSunUpTomorrow() {
		return sunUpTomorrow;
	}

	public void setSunUpTomorrow(final String sunUpTomorrow) {
		this.sunUpTomorrow = sunUpTomorrow;
	}

	public String getSunDownTomorrow() {
		return sunDownTomorrow;
	}

	public void setSunDownTomorrow(final String sunDownTomorrow) {
		this.sunDownTomorrow = sunDownTomorrow;
	}

	public String getMaxTemperatureToday() {
		return maxTemperatureToday;
	}

	public void setMaxTemperatureToday(String maxTemperatureToday) {
		this.maxTemperatureToday = maxTemperatureToday;
	}

	public String getMaxTemperatureTomorrow() {
		return maxTemperatureTomorrow;
	}

	public void setMaxTemperatureTomorrow(String maxTemperatureTomorrow) {
		this.maxTemperatureTomorrow = maxTemperatureTomorrow;
	}

}
