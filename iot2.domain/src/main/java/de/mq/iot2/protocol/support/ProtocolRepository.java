package de.mq.iot2.protocol.support;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.protocol.Protocol;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = ProtocolImpl.class, idClass = String.class)
interface ProtocolRepository {
	Protocol save(@Valid final Protocol protocol);
}
