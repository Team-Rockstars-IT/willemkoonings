package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.api.SongsApi;
import com.wkoonings.rockstarsit.dto.AdditSongRequest;
import com.wkoonings.rockstarsit.dto.SongPageResponse;
import com.wkoonings.rockstarsit.dto.SongResponse;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.service.ArtistService;
import com.wkoonings.rockstarsit.service.SongService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController extends BaseController implements SongsApi {

  private final SongService songService;
  private final ArtistService artistService;

  @Override
  @PostMapping
  public ResponseEntity<SongResponse> createSong(@Valid @RequestBody final AdditSongRequest request) {
    Song song = SongMapper.fromRequest(request);
    final Artist artist = this.artistService.getArtistById(request.getArtistId());
    song.setArtist(artist);
    song = this.songService.createSong(song);
    return ResponseEntity.ok(SongMapper.toResponse(song));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSong(@PathParam("id") final Long id) {
    this.songService.deleteSong(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<SongPageResponse> getAllSongs(@RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "20") Integer size,
                                                      @RequestParam(defaultValue = "id,asc") String sort) {
    final Page<Song> songs = this.songService.getAllSongs(this.getPageableFor(page, size, sort));
    return ResponseEntity.ok(SongMapper.toResponse(songs));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<SongResponse> getSongById(@PathParam("id") final Long id) {
    final Song song = this.songService.getSongById(id);
    return ResponseEntity.ok(SongMapper.toResponse(song));
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<SongResponse> updateSong(@PathParam("id") final Long id, @Valid @RequestBody final AdditSongRequest request) {
    final Song song = this.songService.updateSong(id, SongMapper.fromRequest(request));
    return ResponseEntity.ok(SongMapper.toResponse(song));
  }
}
