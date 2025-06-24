package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.api.ArtistsApi;
import com.wkoonings.rockstarsit.dto.DTOArtist;
import com.wkoonings.rockstarsit.dto.DTOArtistsResponse;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.service.ArtistService;
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
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController implements ArtistsApi {

  private final ArtistService artistService;

  @Override
  @PostMapping
  public ResponseEntity<DTOArtist> createArtist(@Valid @RequestBody final DTOArtist dtOArtist) {
    final Artist artist = this.artistService.createArtist(ArtistMapper.fromDTOArtist(dtOArtist));
    return ResponseEntity.status(201).body(ArtistMapper.toDTOArtist(artist));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArtist(@PathParam("id") final Long id) {
    this.artistService.deleteArtist(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<DTOArtistsResponse> getAllArtists() {
    final List<Artist> artists = this.artistService.getAllArtists();
    return ResponseEntity.ok(ArtistMapper.toDTOArtistsResponse(artists));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<DTOArtist> getArtistById(@PathParam("id") final Long id) {
    final Artist artist = this.artistService.getArtistById(id);
    return ResponseEntity.ok(ArtistMapper.toDTOArtist(artist));
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<DTOArtist> updateArtist(@PathParam("id") final Long id, @Valid @RequestBody final DTOArtist dtOArtist) {
    final Artist artist = this.artistService.updateArtist(id, ArtistMapper.fromDTOArtist(dtOArtist));
    return ResponseEntity.ok(ArtistMapper.toDTOArtist(artist));
  }
}
