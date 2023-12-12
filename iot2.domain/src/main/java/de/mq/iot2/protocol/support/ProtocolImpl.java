package de.mq.iot2.protocol.support;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.util.Assert;
import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity(name = "Protocol")
class ProtocolImpl implements Protocol {

	static final String MESSAGE_EXECUTION_TIME_REQUIRED = "ExecutionTime required.";

	static final String MESSAGE_STATUS_REQUIRED = "Status required.";

	static final String MESSAGE_NAME_IS_REQUIRED = "Name is required.";

	@Id
	@Column(name = "ID", length = 36, nullable = false)
	@Size(min = 36, max = 36)
	private String id;

	@Column(name = "NAME", length = 15, nullable = false)
	@Size(max = 20)
	@NotBlank
	private String name;

	@Column(name = "TIME", nullable = false)
	@NotNull
	private LocalDateTime executionTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATE", length = 15, nullable = false)
	@NotNull
	private Status status;

	@Lob
	@Column(name = "LOG_MESSAGE")
	private String logMessage;

	@SuppressWarnings("unused")
	private ProtocolImpl() {

	}
	
	
	ProtocolImpl(final String id, final String name, LocalDateTime executionTime, final  Status status, final  String logMessage) {
		nameRequiredGuard(name);
		Assert.notNull(id, "Id required.");
		Assert.notNull(executionTime, "ExecutionTime required.");
		Assert.notNull(status, MESSAGE_STATUS_REQUIRED);
		this.id=id;
		this.name=name;
		this.executionTime=executionTime;
		this.status=status;
		this.logMessage=logMessage;
	}

	ProtocolImpl(final String name) {
		nameRequiredGuard(name);
		this.name = name;
		this.executionTime = LocalDateTime.now();
		this.status = Status.Started;
		id = IdUtil.id(executionTime.atZone(ZoneId.systemDefault()).toEpochSecond(), name);
	}

	@Override
	public String name() {
		nameRequiredGuard(name);
		return name;
	}

	private void nameRequiredGuard(final String name) {
		Assert.hasText(name, MESSAGE_NAME_IS_REQUIRED);
	}

	@Override
	public LocalDateTime executionTime() {
		Assert.notNull(executionTime, MESSAGE_EXECUTION_TIME_REQUIRED);
		return executionTime;
	}

	@Override
	public Status status() {
		Assert.notNull(status, MESSAGE_STATUS_REQUIRED);
		return status;
	}

	@Override
	public Optional<String> logMessage() {
		return Optional.ofNullable(logMessage);
	}

	@Override
	public void assignErrorState() {
		stateChangeGuard();
		this.status = Status.Error;
	}

	@Override
	public void assignSuccessState() {
		stateChangeGuard();
		this.status = Status.Success;
	}

	private void stateChangeGuard() {
		Assert.isTrue(this.status == Status.Started, "State should be 'Started'");
	}

	@Override
	public void assignLogMessage(final String logMessage) {
		this.logMessage = logMessage;
	}

	private boolean missingKeyFields(final ProtocolImpl protocol) {
		return (protocol.name == null) || (protocol.executionTime == null);
	}

	@Override
	public int hashCode() {
		if (missingKeyFields(this)) {
			return super.hashCode();
		}
		return name.hashCode() + executionTime.hashCode();
	}

	@Override
	public boolean equals(final Object object) {

		if (!(object instanceof ProtocolImpl)) {
			return super.equals(object);

		}
		final var other = (ProtocolImpl) object;

		if (missingKeyFields(this) || (missingKeyFields(other))) {
			return super.equals(object);
		}

		return other.name.equals(name) && other.executionTime.equals(executionTime);
	}

}
