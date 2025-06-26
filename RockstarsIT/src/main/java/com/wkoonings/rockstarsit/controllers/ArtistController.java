package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.api.ArtistsApi;
import com.wkoonings.rockstarsit.dto.AdditArtistRequest;
import com.wkoonings.rockstarsit.dto.ArtistMapper;
import com.wkoonings.rockstarsit.dto.ArtistPageResponse;
import com.wkoonings.rockstarsit.dto.ArtistResponse;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.service.ArtistService;
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
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController extends BaseController implements ArtistsApi {

  private final ArtistService artistService;

  @Override
  @PostMapping
  public ResponseEntity<ArtistResponse> createArtist(@Valid @RequestBody final AdditArtistRequest request) {
    final Artist artist = this.artistService.createArtist(ArtistMapper.fromRequest(request));
    return ResponseEntity.status(201).body(ArtistMapper.toResponse(artist));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArtist(@PathParam("id") final Long id) {
    this.artistService.deleteArtist(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<ArtistPageResponse> getAllArtists(@RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "20") Integer size,
                                                          @RequestParam(defaultValue = "id,asc") String sort) {
    final Page<Artist> artists = this.artistService.getAllArtists(this.getPageableFor(page, size, sort));
    return ResponseEntity.ok(ArtistMapper.toResponse(artists));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<ArtistResponse> getArtistById(@PathParam("id") final Long id) {
    final Artist artist = this.artistService.getArtistById(id);
    return ResponseEntity.ok(ArtistMapper.toResponse(artist));
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<ArtistResponse> updateArtist(@PathParam("id") final Long id, @Valid @RequestBody final AdditArtistRequest request) {
    final Artist artist = this.artistService.updateArtist(id, ArtistMapper.fromRequest(request));
    return ResponseEntity.ok(ArtistMapper.toResponse(artist));
  }
}
