package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.api.PlaylistsApi;
import com.wkoonings.rockstarsit.dto.AddSongToPlaylistRequest;
import com.wkoonings.rockstarsit.dto.CreatePlaylistRequest;
import com.wkoonings.rockstarsit.dto.PlaylistMapper;
import com.wkoonings.rockstarsit.dto.PlaylistPageResponse;
import com.wkoonings.rockstarsit.dto.PlaylistResponse;
import com.wkoonings.rockstarsit.dto.RemoveSongFromPlaylistRequest;
import com.wkoonings.rockstarsit.model.Playlist;
import com.wkoonings.rockstarsit.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
@Slf4j
public class PlaylistController extends BaseController implements PlaylistsApi {

  private final PlaylistService playlistService;

  @Override
  @PostMapping
  public ResponseEntity<PlaylistResponse> createPlaylist(@Valid @RequestBody final CreatePlaylistRequest createPlaylistRequest) {
    log.info("Creating playlist with name: {}", createPlaylistRequest.getName());

    Playlist createdPlaylist = playlistService.createPlaylist(createPlaylistRequest.getName());
    PlaylistResponse response = PlaylistMapper.toResponse(createdPlaylist, true);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  @GetMapping
  public ResponseEntity<PlaylistPageResponse> getAllPlaylists(
      @RequestParam(defaultValue = "0") final Integer page,
      @RequestParam(defaultValue = "20") final Integer size,
      @RequestParam(defaultValue = "id") final String sort) {
    Page<Playlist> playlists = playlistService.getAllPlaylists(this.getPageableFor(page, size, sort));
    return ResponseEntity.ok(PlaylistMapper.toResponse(playlists));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable final Long id) {
    log.info("Fetching playlist with ID: {}", id);

    Playlist playlist = playlistService.getPlaylistById(id);
    PlaylistResponse response = PlaylistMapper.toResponse(playlist, true);

    return ResponseEntity.ok(response);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePlaylist(@PathVariable final Long id) {
    log.info("Deleting playlist with ID: {}", id);

    playlistService.deletePlaylist(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/{id}/songs")
  public ResponseEntity<PlaylistResponse> addSongToPlaylist(
      @PathVariable final Long id,
      @Valid @RequestBody final AddSongToPlaylistRequest addSongToPlaylistRequest) {

    log.info("Adding song {} to playlist {}", addSongToPlaylistRequest.getSongId(), id);

    Playlist updatedPlaylist = playlistService.addSongToPlaylist(id, addSongToPlaylistRequest.getSongId());
    PlaylistResponse response = PlaylistMapper.toResponse(updatedPlaylist, true);

    return ResponseEntity.ok(response);
  }

  @Override
  @DeleteMapping("/{id}/songs")
  public ResponseEntity<Void> removeSongFromPlaylist(
      @PathVariable final Long id,
      @Valid @RequestBody final RemoveSongFromPlaylistRequest removeSongFromPlaylistRequest) {

    log.info("Removing song {} from playlist {}", removeSongFromPlaylistRequest.getSongId(), id);

    playlistService.removeSongFromPlaylist(id, removeSongFromPlaylistRequest.getSongId());

    return ResponseEntity.noContent().build();
  }
}
