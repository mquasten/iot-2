package de.mq.iot2.configuration.support;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.NotBlank;

public class ConfigurationModel {

	@NotBlank
	private String id;

	private String name;

	private Collection<ParameterModel> parameters = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Collection<ParameterModel> getParameters() {
		return parameters;
	}

	public void setParameters(final Collection<ParameterModel> parameters) {
		this.parameters.clear();
		if (CollectionUtils.isEmpty(parameters)) {
			return;
		}
		this.parameters.addAll(parameters);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
