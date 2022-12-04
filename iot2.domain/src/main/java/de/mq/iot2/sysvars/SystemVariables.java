package de.mq.iot2.sysvars;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "systemVariables")
public class SystemVariables {

	@XmlElement(name = "systemVariable", type = SystemVariable.class)
	private List<SystemVariable> systemVariables = new ArrayList<SystemVariable>();

	public List<SystemVariable> getSystemVariables() {
		return systemVariables;
	}

	public void setSystemVariables(List<SystemVariable> systemVariables) {
		this.systemVariables = systemVariables;
	}

	

	

}
