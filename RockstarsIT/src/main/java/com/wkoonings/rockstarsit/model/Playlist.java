package com.wkoonings.rockstarsit.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"songs"})
@ToString(exclude = {"songs"})
public class Playlist {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_id_seq")
  @SequenceGenerator(name = "playlist_id_seq", sequenceName = "playlist_id_seq", allocationSize = 50)
  private Long id;

  @Column(nullable = false)
  @NotBlank(message = "Name is required")
  @Size(min = 2, message = "Name must be longer than 2 characters")
  private String name;

  @Column(nullable = false)
  @NotBlank(message = "Username is required")
  @Size(min = 2, message = "Username must be longer than 2 characters")
  private String username;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "playlist_songs",
      joinColumns = @JoinColumn(name = "playlist_id"),
      inverseJoinColumns = @JoinColumn(name = "song_id")
  )
  @Builder.Default
  private Set<Song> songs = new HashSet<>();

  public Playlist(final String name, final String username) {
    this.name = name;
    this.username = username;
  }

  public void addSong(Song song) {
    this.songs.add(song);
    song.getPlaylists().add(this);
  }

  public void removeSong(Song song) {
    this.songs.remove(song);
    song.getPlaylists().remove(this);
  }
}
