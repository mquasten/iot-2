package de.mq.iot2.calendar.support;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.NotBlank;

public class DayGroupModel {
	@NotBlank
	private String id;

	private String name;

	private boolean readonly;

	private String cycleId;

	private Collection<DayModel> days = new ArrayList<>();

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getCycleId() {
		return cycleId;
	}

	public void setCycleId(String cycleId) {
		this.cycleId = cycleId;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Collection<DayModel> getDays() {
		return days;
	}

	public void setDays(final Collection<DayModel> days) {
		this.days.clear();
		if (CollectionUtils.isEmpty(days)) {
			return;
		}
		this.days.addAll(days);
	}

}
