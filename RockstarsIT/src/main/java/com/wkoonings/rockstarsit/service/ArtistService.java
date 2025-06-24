package com.wkoonings.rockstarsit.service;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.persistence.ArtistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ArtistService {

  private final ArtistRepository artistRepository;

  @Transactional
  public Artist createArtist(Artist artist) {
    log.info("Creating new artist: {}", artist.getName());
    return artistRepository.save(artist);
  }

  @Transactional
  public List<Artist> createArtists(List<Artist> artists) {
    log.info("Creating new artists: {}", artists.size());
    return artistRepository.saveAll(artists);
  }

  @Transactional
  public Artist updateArtist(Long id, Artist artist) {
    log.info("Updating artist with ID: {}", id);
    if (!artistRepository.existsById(id)) {
      throw new RuntimeException("Artist not found with ID: " + id);
    }
    artist.setId(id);
    return artistRepository.save(artist);
  }

  public List<Artist> getAllArtists() {
    log.info("Fetching all artists");
    return artistRepository.findAll();
  }

  @Transactional
  public Artist getArtistById(Long id) {
    log.info("Fetching artist with ID: {}", id);
    return artistRepository.findById(id)
                           .orElseThrow(() -> new RuntimeException("Artist not found with ID: " + id));
  }
}
