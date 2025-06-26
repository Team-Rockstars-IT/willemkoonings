package com.wkoonings.rockstarsit.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"songs"})
public class Artist {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_id_seq")
  @SequenceGenerator(name = "artist_id_seq", sequenceName = "artist_id_seq", allocationSize = 50)
  private Long id;

  @Column(nullable = false)
  @NotBlank(message = "Name is required")
  @Size(min = 2, message = "Name must be longer than 2 characters")
  private String name;

  @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<Song> songs = new ArrayList<>();

  @Column
  private Long externalId;

  public Artist(final String name, final List<Song> songs) {
    this.name = name;
    this.songs = songs;
  }
}
