package de.mq.iot2.protocol.support;

import java.util.Collection;
import java.util.List;

public class ProtocolModel {
	
	private String name;
	



	private Collection<ProtocolModel> protocols = List.of();

	public Collection<ProtocolModel> getProtocols() {
		return protocols;
	}

	public void setProtocols(Collection<ProtocolModel> protocols) {
		this.protocols = protocols;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
