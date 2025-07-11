package com.wkoonings.rockstarsit.service;

import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.persistence.SongRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
public class SongService {

  private final SongRepository songRepository;
  private final JdbcBatchService jdbcBatchService;

  @Transactional
  public Song createSong(Song song) {
    log.info("Creating new song: {}", song.getName());
    return songRepository.save(song);
  }

  @Transactional
  public List<Song> createSongs(List<Song> songs) {
    return this.jdbcBatchService.batchInsertSongs(songs);
  }

  @Transactional
  public Song updateSong(Long id, Song song) {
    log.info("Updating song with ID: {}", id);
    if (!songRepository.existsById(id)) {
      throw new EntityNotFoundException("Song not found with ID: " + id);
    }
    song.setId(id);
    return songRepository.save(song);
  }

  public Page<Song> getAllSongs(Pageable pageable) {
    log.info("Fetching all songs");
    return songRepository.findAll(pageable);
  }

  public Song getSongById(Long id) {
    log.info("Fetching song with ID: {}", id);
    return songRepository.findById(id)
                         .orElseThrow(() -> new EntityNotFoundException("Song not found with ID: " + id));
  }

  @Transactional
  public void deleteSong(Long id) {
    log.info("Deleting song with ID: {}", id);
    if (!songRepository.existsById(id)) {
      throw new EntityNotFoundException("Song not found with ID: " + id);
    }
    songRepository.deleteById(id);
  }

  public List<Long> findExternalIdsByExternalIds(List<Long> externalIds) {
    // Return only the external IDs that exist in the database
    return songRepository.findExternalIdsByExternalIdIn(externalIds);
  }
}
