package de.mq.iot2.sysvars;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.util.Assert;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "systemVariable")
public class SystemVariable {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String value;
	@XmlAttribute(name = "ise_id")
	private String id;
	public SystemVariable() {
		
	}
	public SystemVariable(final String name, final String value) {
		Assert.notNull(name, "Name required.");
		Assert.notNull(value, "Value required.");
		this.name=name;
		this.value=value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


}
