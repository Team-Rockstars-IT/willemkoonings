package com.wkoonings.rockstarsit.service;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.persistence.ArtistRepository;
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
public class ArtistService {

  private final ArtistRepository artistRepository;
  private final JdbcBatchService jdbcBatchService;

  @Transactional
  public Artist createArtist(Artist artist) {
    log.info("Creating new artist: {}", artist.getName());
    return artistRepository.save(artist);
  }

  @Transactional
  public List<Artist> createArtists(List<Artist> artists) {
    return this.jdbcBatchService.batchInsertArtists(artists);
  }

  @Transactional
  public Artist updateArtist(Long id, Artist artist) {
    log.info("Updating artist with ID: {}", id);
    if (!artistRepository.existsById(id)) {
      throw new EntityNotFoundException("Artist not found with ID: " + id);
    }
    artist.setId(id);
    return artistRepository.save(artist);
  }

  public Page<Artist> getAllArtists(Pageable pageable) {
    log.info("Fetching all artists");
    return artistRepository.findAll(pageable);
  }

  public Artist getArtistById(Long id) {
    log.info("Fetching artist with ID: {}", id);
    return artistRepository.findById(id)
                           .orElseThrow(() -> new EntityNotFoundException("Artist not found with ID: " + id));
  }

  public Artist getArtistByName(String name) {
    log.info("Fetching artist with name: {}", name);
    return artistRepository.findByName(name)
                           .orElseThrow(() -> new EntityNotFoundException("Artist not found with name: " + name));
  }

  @Transactional
  public void deleteArtist(Long id) {
    log.info("Deleting artist with ID: {}", id);
    if (!artistRepository.existsById(id)) {
      throw new EntityNotFoundException("Artist not found with ID: " + id);
    }
    artistRepository.deleteById(id);
  }

  public List<Long> findExternalIdsByExternalIds(final List<Long> externalIds) {
    return artistRepository.findExternalIdsByExternalIdIn(externalIds);
  }

  public List<Artist> findByExternalIds(final List<Long> externalIds) {
    return artistRepository.findByExternalIdIn(externalIds);
  }
}
