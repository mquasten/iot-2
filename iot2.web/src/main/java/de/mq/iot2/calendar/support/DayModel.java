package de.mq.iot2.calendar.support;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;

@ValidDayModel
public class DayModel implements Comparable<DayModel> {

	private String id;
	private String value;
	private String valueSorted;
	private String dayGroupId;
	private String type;
	private Object targetValue;
	private String description;

	Class<?> targetEntity() {
		Assert.hasText(type, "Type is required.");
		try {
			return Class.forName(this.getType());
		} catch (final ClassNotFoundException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(Object targetValue) {
		this.targetValue = targetValue;
	}

	public String getDayGroupId() {
		return dayGroupId;
	}

	public void setDayGroupId(String dayGroupId) {
		this.dayGroupId = dayGroupId;
	}

	public void setValueSorted(String valueSorted) {
		this.valueSorted = valueSorted;
	}

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
