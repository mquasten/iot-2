package de.mq.iot2.protocol.support;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

import de.mq.iot2.protocol.Protocol.Status;

public class ProtocolModel {
	
	private String id;

	private String name;
	
	


	private String logMessage;

	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public boolean isLogMessageAware() {
		return  StringUtils.hasText(logMessage);
	}


	private LocalDateTime executionTime;
	
	private Status status; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(LocalDateTime executionTime) {
		this.executionTime = executionTime;
	}

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
