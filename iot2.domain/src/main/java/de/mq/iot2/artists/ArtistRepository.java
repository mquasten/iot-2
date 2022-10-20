package de.mq.iot2.artists;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = ArtistImpl.class, idClass = Long.class)
public interface ArtistRepository {
	Collection<Artist> findAll();
	Optional<Artist> findByName(final String name);
	
	// start the databaseserver: java -jar h2-2.1.214.jar -tcpAllowOthers  -baseDir C:\mq/h2/
}
