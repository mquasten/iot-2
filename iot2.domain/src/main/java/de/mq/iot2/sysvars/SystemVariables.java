package de.mq.iot2.sysvars;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
