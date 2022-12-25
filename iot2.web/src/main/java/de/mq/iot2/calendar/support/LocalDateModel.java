package de.mq.iot2.calendar.support;

import jakarta.validation.constraints.NotBlank;

@ValidLocalDateModel
public class LocalDateModel {
	@NotBlank
	private String from;
	private String to;
	private String dayGroupId;
	private String dayGroupName;
	
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
