package com.wkoonings.rockstarsit.service;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.persistence.ArtistRepository;
import com.wkoonings.rockstarsit.persistence.SongRepository;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdbcBatchService {

  private final ArtistRepository artistRepository;
  private final SongRepository songRepository;
  private final JdbcTemplate jdbcTemplate;

  public List<Artist> batchInsertArtists(List<Artist> artists) {
    log.info("Batch inserting {} artists using JDBC", artists.size());

    String sql = "INSERT INTO artists (id, name, external_id) VALUES (nextval('artist_id_seq'), ?, ?)";

    jdbcTemplate.batchUpdate(sql,
                             artists,
                             50, // batch size
                             (PreparedStatement ps, Artist artist) -> {
                               ps.setString(1, artist.getName());
                               ps.setLong(2, artist.getExternalId());
                             });

    List<Artist> savedArtists = this.artistRepository.findByExternalIdIn(artists.stream().map(Artist::getExternalId).toList());

    log.info("Successfully batch inserted {} artists", artists.size());
    return savedArtists;
  }

  public List<Song> batchInsertSongs(List<Song> songs) {
    log.info("Batch inserting {} songs using JDBC", songs.size());

    String sql = "INSERT INTO songs (id, name, \"year\", artist_id, shortname, bpm, duration, genre, spotify_id, album, external_id) VALUES (nextval('song_id_seq'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.batchUpdate(sql,
                             songs,
                             50, // batch size
                             (PreparedStatement ps, Song song) -> {
                               ps.setString(1, song.getName());
                               ps.setInt(2, song.getYear());
                               ps.setLong(3, song.getArtist().getId());
                               ps.setString(4, song.getShortname());
                               ps.setInt(5, song.getBpm());
                               ps.setInt(6, song.getDuration());
                               ps.setString(7, song.getGenre());
                               ps.setString(8, song.getSpotifyId());
                               ps.setString(9, song.getAlbum());
                               ps.setLong(10, song.getExternalId());
                             });

    List<Song> savedSongs = this.songRepository.findByExternalIdIn(songs.stream().map(Song::getExternalId).toList());

    log.info("Successfully batch inserted {} songs", songs.size());
    return savedSongs;
  }
}
