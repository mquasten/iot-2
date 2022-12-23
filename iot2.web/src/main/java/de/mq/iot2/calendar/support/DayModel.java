package de.mq.iot2.calendar.support;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;

public class DayModel  implements Comparable<DayModel>{

	private String id;
	private String value;
	private String valueSorted;

	public void setValueSorted(String valueSorted) {
		this.valueSorted = valueSorted;
	}

	private String description;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public int compareTo(final DayModel other) {
		return Objects.requireNonNullElse(valueSorted, Strings.EMPTY).compareTo(Objects.requireNonNullElse(other.valueSorted, Strings.EMPTY));
	}

	

}
