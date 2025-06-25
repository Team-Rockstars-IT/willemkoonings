package com.wkoonings.rockstarsit.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.persistence.ArtistRepository;
import com.wkoonings.rockstarsit.service.ArtistService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistService Unit Tests")
public class ArtistServiceUT {

  @Mock
  private ArtistRepository artistRepository;

  @InjectMocks
  private ArtistService artistService;

  private Artist testArtist;

  @BeforeEach
  void setUp() {
    testArtist = Artist.builder()
                       .id(1L)
                       .name("Blue Öyster Cult")
                       .build();
  }

  @Test
  @DisplayName("Should create artist successfully")
  void shouldCreateArtistSuccessfully() {
    // Given
    Artist artistToCreate = Artist.builder()
                                  .name("Blue Öyster Cult")
                                  .build();

    when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);

    // When
    Artist createdArtist = artistService.createArtist(artistToCreate);

    // Then
    assertThat(createdArtist).isNotNull();
    assertThat(createdArtist.getId()).isEqualTo(1L);
    assertThat(createdArtist.getName()).isEqualTo("Blue Öyster Cult");
    verify(artistRepository).save(artistToCreate);
  }

  @Test
  @DisplayName("Should create multiple artists successfully")
  void shouldCreateMultipleArtistsSuccessfully() {
    // Given
    Artist artist2 = Artist.builder()
                           .name("Led Zeppelin")
                           .build();
    List<Artist> artistsToCreate = Arrays.asList(testArtist, artist2);

    when(artistRepository.saveAll(any(List.class))).thenReturn(artistsToCreate);

    // When
    List<Artist> createdArtists = artistService.createArtists(artistsToCreate);

    // Then
    assertThat(createdArtists.size()).isEqualTo(2);
    assertThat(createdArtists.get(0).getName()).isEqualTo("Blue Öyster Cult");
    assertThat(createdArtists.get(1).getName()).isEqualTo("Led Zeppelin");
    verify(artistRepository).saveAll(artistsToCreate);
  }

  @Test
  @DisplayName("Should update artist successfully")
  void shouldUpdateArtistSuccessfully() {
    // Given
    Artist updatedArtist = Artist.builder()
                                 .id(1L)
                                 .name("Updated Blue Öyster Cult")
                                 .build();
    when(artistRepository.existsById(1L)).thenReturn(true);
    when(artistRepository.save(any(Artist.class))).thenReturn(updatedArtist);

    // When
    Artist result = artistService.updateArtist(1L, updatedArtist);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Updated Blue Öyster Cult");
    verify(artistRepository).existsById(1L);
    verify(artistRepository).save(any(Artist.class));
  }

  @Test
  @DisplayName("Should throw exception when updating non-existing artist")
  void shouldThrowExceptionWhenUpdatingNonExistingArtist() {
    // Given
    Artist updatedArtist = Artist.builder()
                                 .id(999L)
                                 .name("Non-existing Artist")
                                 .build();
    when(artistRepository.existsById(999L)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> artistService.updateArtist(999L, updatedArtist))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Artist not found with ID: 999");
    verify(artistRepository).existsById(999L);
  }

  @Test
  @DisplayName("Should find all artists")
  void shouldFindAllArtists() {
    // Given
    Artist artist2 = Artist.builder()
                           .id(2L)
                           .name("Led Zeppelin")
                           .build();

    List<Artist> artists = Arrays.asList(testArtist, artist2);
    when(artistRepository.findAll()).thenReturn(artists);

    // When
    List<Artist> foundArtists = artistService.getAllArtists(PageRequest.of(0, 10, null)).getContent();

    // Then
    assertThat(foundArtists.size()).isEqualTo(2L);
    assertThat(foundArtists.get(0).getName()).isEqualTo("Blue Öyster Cult");
    assertThat(foundArtists.get(1).getName()).isEqualTo("Led Zeppelin");
    verify(artistRepository).findAll();
  }

  @Test
  @DisplayName("Should find artist by id")
  void shouldFindArtistById() {
    // Given
    when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));

    // When
    Artist foundArtist = artistService.getArtistById(1L);

    // Then
    assertThat(foundArtist).isNotNull();
    assertThat(foundArtist.getId()).isEqualTo(1L);
    assertThat(foundArtist.getName()).isEqualTo("Blue Öyster Cult");
    verify(artistRepository).findById(1L);
  }

  @Test
  @DisplayName("Should throw exception when artist not found by id")
  void shouldThrowExceptionWhenArtistNotFoundById() {
    // Given
    when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> artistService.getArtistById(999L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Artist not found with ID: 999");
    verify(artistRepository).findById(999L);
  }
}
