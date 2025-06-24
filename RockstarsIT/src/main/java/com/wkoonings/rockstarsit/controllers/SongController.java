package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.api.SongsApi;
import com.wkoonings.rockstarsit.dto.DTOSong;
import com.wkoonings.rockstarsit.dto.DTOSongsResponse;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.service.ArtistService;
import com.wkoonings.rockstarsit.service.SongService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController implements SongsApi {

  private final SongService songService;
  private final ArtistService artistService;

  @Override
  @PostMapping
  public ResponseEntity<DTOSong> createSong(@Valid @RequestBody final DTOSong dtOSong) {
    Song song = SongMapper.fromDTOSong(dtOSong);
    final Artist artist = this.artistService.getArtistByName(dtOSong.getArtistName());
    song.setArtist(artist);
    song = this.songService.createSong(song);
    return ResponseEntity.ok(SongMapper.toDTOSong(song));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSong(@PathParam("id") final Long id) {
    this.songService.deleteSong(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<DTOSongsResponse> getAllSongs() {
    final List<Song> songs = this.songService.getAllSongs();
    return ResponseEntity.ok(SongMapper.toDTOSongsResponse(songs));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<DTOSong> getSongById(@PathParam("id") final Long id) {
    final Song song = this.songService.getSongById(id);
    return ResponseEntity.ok(SongMapper.toDTOSong(song));
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<DTOSong> updateSong(@PathParam("id") final Long id, @Valid @RequestBody final DTOSong dtOSong) {
    final Song song = this.songService.updateSong(id, SongMapper.fromDTOSong(dtOSong));
    return ResponseEntity.ok(SongMapper.toDTOSong(song));
  }
}
