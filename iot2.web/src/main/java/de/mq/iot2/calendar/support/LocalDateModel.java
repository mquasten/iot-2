package de.mq.iot2.calendar.support;

import java.time.LocalDate;

import org.springframework.util.Assert;

@ValidLocalDateModel
public class LocalDateModel {
	private String from;
	private String to;
	private String dayGroupId;
	private String dayGroupName;

	private LocalDate fromDate;

	private LocalDate toDate;

	public LocalDate getFromDate() {
		Assert.notNull(fromDate, "FromDate is required.");
		return fromDate;
	}

	public void setFromDate(final LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		Assert.notNull(toDate, "ToDate is required.");
		return toDate;
	}

	public void setToDate(final LocalDate toDate) {
		this.toDate = toDate;
	}

	public String getDayGroupId() {
		return dayGroupId;
	}

	public void setDayGroupId(String dayGroupId) {
		this.dayGroupId = dayGroupId;
	}

	public String getDayGroupName() {
		return dayGroupName;
	}

	public void setDayGroupName(String dayGroupName) {
		this.dayGroupName = dayGroupName;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
