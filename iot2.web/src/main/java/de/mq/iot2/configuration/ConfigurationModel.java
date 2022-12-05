package de.mq.iot2.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.CollectionUtils;

public class ConfigurationModel {

	private String configurationId;

	private String name;

	private Collection<ParameterModel> parameters = new ArrayList<>();

	public Collection<ParameterModel> getParameters() {
		return parameters;
	}

	public void setParameters(Collection<ParameterModel> parameters) {
		this.parameters.clear();
		if (CollectionUtils.isEmpty(parameters)) {
			return;
		}
		this.parameters.addAll(parameters);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}

}
