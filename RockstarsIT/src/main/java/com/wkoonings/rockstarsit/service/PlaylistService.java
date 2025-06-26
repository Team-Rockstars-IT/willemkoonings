package com.wkoonings.rockstarsit.service;

import com.wkoonings.rockstarsit.auth.SecurityContextUtil;
import com.wkoonings.rockstarsit.model.Playlist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.persistence.PlaylistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlaylistService {

  private final PlaylistRepository playlistRepository;
  private final SongService songService;

  @Transactional
  public Playlist createPlaylist(String name) {
    log.info("Creating new playlist: {}", name);
    return playlistRepository.save(new Playlist(name, SecurityContextUtil.getLoggedInUsername()));
  }

  @Transactional
  public void deletePlaylist(Long id) {
    log.info("Deleting playlist with ID: {}", id);
    if (!playlistRepository.existsByIdAndName(id, SecurityContextUtil.getLoggedInUsername())) {
      throw new EntityNotFoundException("Playlist not found with ID: " + id);
    }
    playlistRepository.deleteByIdAndName(id, SecurityContextUtil.getLoggedInUsername());
  }

  public Playlist getPlaylistById(Long id) {
    log.info("Fetching playlist with ID: {}", id);
    return this.getById(id);
  }

  public Page<Playlist> getAllPlaylists(Pageable pageable) {
    log.info("Fetching all playlists");
    return playlistRepository.findAllByUsername(SecurityContextUtil.getLoggedInUsername(), pageable);
  }

  @Transactional
  public Playlist addSongToPlaylist(Long playlistId, Long songId) {
    log.info("Adding song with ID {} to playlist with ID {}", songId, playlistId);

    Playlist playlist = this.getById(playlistId);
    Song song = songService.getSongById(songId);

    playlist.addSong(song);
    return playlistRepository.save(playlist);
  }

  @Transactional
  public void removeSongFromPlaylist(Long playlistId, Long songId) {
    log.info("Removing song with ID {} from playlist with ID {}", songId, playlistId);

    Playlist playlist = this.getById(playlistId);
    Song song = songService.getSongById(songId);

    playlist.removeSong(song);
    playlistRepository.save(playlist);
  }

  private Playlist getById(final Long id) {
    log.info("Authenticated user: {}", SecurityContextUtil.getLoggedInUsername());
    return playlistRepository.findByIdAndUsername(id, SecurityContextUtil.getLoggedInUsername())
                             .orElseThrow(() -> new EntityNotFoundException("Playlist not found with ID: " + id));
  }
}
