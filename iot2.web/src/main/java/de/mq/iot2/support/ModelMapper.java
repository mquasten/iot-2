package de.mq.iot2.support;

import java.util.Collection;
import java.util.stream.Collectors;

public interface ModelMapper<Domain,Web> {
	
	Web toWeb(final Domain domain);
	
	default Web toWeb(final String id) {
		return toWeb(toDomain(id));
	}
	
	default Collection<Web> toWeb(final Collection<Domain> domains){
		return domains.stream().map(this::toWeb).collect(Collectors.toList());
	}
	
	default Domain toDomain(final Web web ) {
		throw new UnsupportedOperationException("Method not implemented.");
	}
	
	default Domain toDomain(final String id) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

}
