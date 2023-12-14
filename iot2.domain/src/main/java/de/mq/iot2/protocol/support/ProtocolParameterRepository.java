package de.mq.iot2.protocol.support;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import de.mq.iot2.protocol.Protocol;
import de.mq.iot2.protocol.ProtocolParameter;
import de.mq.iot2.protocol.SystemvariableProtocolParameter;
import jakarta.validation.Valid;

@RepositoryDefinition(domainClass = ProtocolParameterImpl.class, idClass = ProtocolParameterPrimaryKeyImpl.class)
interface ProtocolParameterRepository {
	
	Collection<ProtocolParameter> findAll();
	
	ProtocolParameter save(@Valid final ProtocolParameter protocolParameter);
	
	@Query("select p  from SystemvariableProtocolParameter p where p.protocol.id= ?1 and p.name in ?2")
	Collection<SystemvariableProtocolParameter> findByProtocolIdNameNameIn(final String protocolId, final List<String> names);
	
	void delete(final ProtocolParameter protocolParameter);
	
	Collection<ProtocolParameter> findByProtocolOrderByTypeAscNameAsc(final Protocol protocol);
	
	
	@Modifying
	@Query("delete from ProtocolParameter")
	void deleteAll();


}
