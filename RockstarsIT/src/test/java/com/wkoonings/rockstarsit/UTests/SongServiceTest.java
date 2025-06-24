package com.wkoonings.rockstarsit.UTests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.persistence.SongRepository;
import com.wkoonings.rockstarsit.service.SongService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistService Unit Tests")
public class SongServiceTest {

  @Mock
  private SongRepository songRepository;

  @InjectMocks
  private SongService songService;

  private Song testSong;

  @BeforeEach
  void setUp() {
    testSong = Song.builder()
                   .id(1L)
                   .name("Don't Fear the Reaper")
                   .build();
  }

  @Test
  @DisplayName("Should create song successfully")
  void shouldCreateSongSuccessfully() {
    // Given
    Song songToCreate = Song.builder()
                            .name("Don't Fear the Reaper")
                            .build();

    when(songRepository.save(any(Song.class))).thenReturn(testSong);

    // When
    Song createdSong = songService.createSong(songToCreate);

    // Then
    assertNotNull(createdSong);
    assertNotNull(createdSong.getId());
    assertNotNull(createdSong.getName());
    verify(songRepository).save(any(Song.class));
  }

  @Test
  @DisplayName("Should create multiple songs successfully")
  void shouldCreateMultipleSongsSuccessfully() {
    // Given
    Song song1 = Song.builder().name("Song 1").build();
    Song song2 = Song.builder().name("Song 2").build();

    when(songRepository.saveAll(any())).thenReturn(List.of(testSong, testSong));

    // When
    List<Song> createdSongs = songService.createSongs(List.of(song1, song2));

    // Then
    assertNotNull(createdSongs);
    assertNotNull(createdSongs.get(0).getId());
    assertNotNull(createdSongs.get(0).getName());
    verify(songRepository).saveAll(any());
  }

  @Test
  @DisplayName("should update song successfully")
  void shouldUpdateSongSuccessfully() {
    // Given
    Song songToUpdate = Song.builder()
                            .name("Don't Fear the Reaper - Updated")
                            .build();

    when(songRepository.existsById(1L)).thenReturn(true);
    when(songRepository.save(any(Song.class))).thenReturn(testSong);

    // When
    Song updatedSong = songService.updateSong(1L, songToUpdate);

    // Then
    assertNotNull(updatedSong);
    assertNotNull(updatedSong.getId());
    assertNotNull(updatedSong.getName());
    verify(songRepository).existsById(1L);
    verify(songRepository).save(any(Song.class));
  }

  @Test
  @DisplayName("Should retrieve song by ID successfully")
  void shouldRetrieveSongByIdSuccessfully() {
    // Given
    when(songRepository.findById(1L)).thenReturn(java.util.Optional.of(testSong));

    // When
    Song retrievedSong = songService.getSongById(1L);

    // Then
    assertNotNull(retrievedSong);
    assertNotNull(retrievedSong.getId());
    assertNotNull(retrievedSong.getName());
    verify(songRepository).findById(1L);
  }
}