package de.mq.iot2.configuration.support;

import jakarta.validation.constraints.NotBlank;

@ValidParameter
public class ParameterModel {

	private String id;

	private String name;

	@NotBlank
	private String value;

	private String configuration;

	private String configurationId;

	private String cycle;

	private String cycleId;

	public String getCycleId() {
		return cycleId;
	}

	public void setCycleId(final String cycleId) {
		this.cycleId = cycleId;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(final String cycle) {
		this.cycle = cycle;
	}

	public String getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(final String configurationId) {
		this.configurationId = configurationId;
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

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(final String configuration) {
		this.configuration = configuration;
	}

}
