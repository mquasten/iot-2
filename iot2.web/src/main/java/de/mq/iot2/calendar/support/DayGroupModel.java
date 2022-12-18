package de.mq.iot2.calendar.support;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.NotBlank;

public class DayGroupModel {
	@NotBlank
	private String id;
	
	private String name; 
	
	private Collection<DayModel> days = new ArrayList<>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<DayModel> getDays() {
		return days;
	}

	public void setParameters(final Collection<DayModel> days) {
		this.days.clear();
		if (CollectionUtils.isEmpty(days)) {
			return;
		}
		this.days.addAll(days);
	}


}
