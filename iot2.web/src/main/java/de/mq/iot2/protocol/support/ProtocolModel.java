package de.mq.iot2.protocol.support;

import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

import de.mq.iot2.protocol.Protocol.Status;

public class ProtocolModel {
	
	private static final String TRUNCATED_POSTFIX = "...";

	private static final int SHORT_LOG_MESSAGE_LENGTH = 50;


	private String id;

	private String name;
	
	private String executionTime;


	private Status status; 
	

	private String logMessage;
	
	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}


	public String getLogMessage() {
		return logMessage;
	}
	
	public String getLogMessageShort() {
		if(logMessage==null) {
			return logMessage;
		}
		
		if (logMessage.length() > SHORT_LOG_MESSAGE_LENGTH) {
			return logMessage.subSequence(0, SHORT_LOG_MESSAGE_LENGTH) + TRUNCATED_POSTFIX;
		}
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public boolean isLogMessageAware() {
		return  StringUtils.hasText(logMessage);
	}



	
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
