package de.mq.iot2.protocol.support;

import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.protocol.ProtocolParameter;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = ProtocolParameterImpl.class, idClass = ProtocolParameterPrimaryKeyImpl.class)
interface ProtocolParameterRepository {
	
	ProtocolParameter save(@Valid final ProtocolParameter protocolParameter);

}
