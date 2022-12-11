package de.mq.iot2.support;

import java.util.Collection;
import java.util.stream.Collectors;

public interface ModelMapper<Domain,Web> {
	
	Web toWeb(Domain domain);
	
	default Web toWeb(String id) {
		return toWeb(toDomain(id));
	}
	
	default Collection<Web> toWeb(Collection<Domain> domains){
		return domains.stream().map(this::toWeb).collect(Collectors.toList());
	}
	
	default Domain toDomain(Web web ) {
		throw new UnsupportedOperationException("Method not implemented");
	}
	
	default Domain toDomain(final String id) {
		throw new UnsupportedOperationException("Method not implemented");
	}

}
