package de.mq.iot2.artists;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Artist")
@Table(name = "artists")
class ArtistImpl implements Artist {

	@Id
	Long id;

	String name;

	Integer score;

	ArtistImpl() {

	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Integer score() {
		return score;
	}

}
