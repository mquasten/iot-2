package de.mq.iot2.batch.support;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.mq.iot2.artists.Artist;
import de.mq.iot2.artists.ArtistRepository;

@SpringBootApplication
@EnableJpaRepositories("de.mq.iot2.artists")
@EntityScan(basePackages = "de.mq.iot2.artists")
public class SpringBootConsoleApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	private final ArtistRepository artistRepository;

	SpringBootConsoleApplication(final ArtistRepository artistRepository) {
		this.artistRepository = artistRepository;
	}

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(SpringBootConsoleApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {

		//LOG.info("EXECUTING : command line runner");

		artistRepository.findByName("Athina")
				.ifPresent(artist -> System.out.printf("%s has the score %d.\n", artist.name(), artist.score()));

		Collection<Artist> artists = artistRepository.findAll();

		System.out.printf("%d artist in  database.\n", artists.size());
		
		artists.forEach(artist -> System.out.printf("%s has the score %d.\n", artist.name(), artist.score()));

	}
}
