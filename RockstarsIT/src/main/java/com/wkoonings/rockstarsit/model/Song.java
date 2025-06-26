package com.wkoonings.rockstarsit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Song {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_seq")
  @SequenceGenerator(name = "song_id_seq", sequenceName = "song_id_seq", allocationSize = 50)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(nullable = false)
  @NotBlank(message = "Name is required")
  @Size(min = 2, message = "Name must be longer than 2 characters")
  private String name;

  @Column(name = "`year`", nullable = false)
  private int year;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artist_id", nullable = false)
  @JsonIgnore
  private Artist artist;

  @Column(nullable = false)
  @Size(min = 2, message = "Shortname must be longer than 2 characters")
  private String shortname;

  @Column
  @Size(min = 1, max = 4)
  private int bpm;

  @Column(nullable = false)
  @Size(min = 1, max = 10)
  private int duration;

  @Column(nullable = false)
  private String genre;

  @Column(name = "spotify_id")
  private String spotifyId;

  @Column
  private String album;

  @Column
  private Long externalId;

  public Song(final String name, final int year, final Artist artist, final String shortname,
              final int bpm, final int duration, final String genre, final String spotifyId,
              final String album) {
    this.name = name;
    this.year = year;
    this.artist = artist;
    this.shortname = shortname;
    this.bpm = bpm;
    this.duration = duration;
    this.genre = genre;
    this.spotifyId = spotifyId;
    this.album = album;
  }
}
