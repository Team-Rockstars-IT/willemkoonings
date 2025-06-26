package com.wkoonings.rockstarsit.unit;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wkoonings.rockstarsit.auth.SecurityContextUtil;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Playlist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.persistence.PlaylistRepository;
import com.wkoonings.rockstarsit.service.PlaylistService;
import com.wkoonings.rockstarsit.service.SongService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaylistService Unit Tests")
public class PlaylistServiceUT {

  @Mock
  private PlaylistRepository playlistRepository;

  @Mock
  private SongService songService;

  @InjectMocks
  private PlaylistService playlistService;

  private Playlist playlist;
  private Song song;
  private Artist artist;
  private static final String TEST_USERNAME = "testuser";
  private static final String TEST_PLAYLIST_NAME = "Test Playlist";

  @BeforeEach
  void setUp() {
    artist = Artist.builder()
                   .id(1L)
                   .name("Test Artist")
                   .build();

    playlist = Playlist.builder()
                       .id(1L)
                       .name(TEST_PLAYLIST_NAME)
                       .username(TEST_USERNAME)
                       .build();

    song = Song.builder()
               .id(1L)
               .name("Test Song")
               .year(2023)
               .shortname("testsong")
               .bpm(120)
               .duration(240000)
               .genre("Rock")
               .album("Test Album")
               .artist(artist)
               .build();
  }

  @Test
  @DisplayName("Should create playlist successfully")
  void shouldCreatePlaylist() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);

      // Act
      Playlist created = playlistService.createPlaylist(TEST_PLAYLIST_NAME);

      // Assert
      assertNotNull(created);
      assertEquals(playlist.getName(), created.getName());
      assertEquals(playlist.getUsername(), created.getUsername());
      verify(playlistRepository, times(1)).save(any(Playlist.class));
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(1));
    }
  }

  @Test
  @DisplayName("Should get all playlists for logged-in user")
  void shouldGetAllPlaylists() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      List<Playlist> playlists = List.of(playlist);

      // Fix: Use eq() matcher for TEST_USERNAME since you're already using any() for Pageable
      when(playlistRepository.findAllByUsername(eq(TEST_USERNAME), any(Pageable.class)))
          .thenReturn(new PageImpl<>(playlists, PageRequest.of(0, 10), playlists.size()));

      // Act
      Page<Playlist> result = playlistService.getAllPlaylists(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")));
      List<Playlist> actualPlaylists = result.getContent();

      // Assert
      assertNotNull(playlists);
      assertEquals(1, playlists.size());
      assertEquals(playlist.getName(), playlists.get(0).getName());
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(1));
    }
  }

  @Test
  @DisplayName("Should get playlist by ID successfully")
  void shouldGetPlaylistById() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.findByIdAndUsername(1L, TEST_USERNAME)).thenReturn(Optional.of(playlist));

      // Act
      Playlist found = playlistService.getPlaylistById(1L);

      // Assert
      assertNotNull(found);
      assertEquals(playlist.getName(), found.getName());
      assertEquals(playlist.getUsername(), found.getUsername());
      verify(playlistRepository, times(1)).findByIdAndUsername(1L, TEST_USERNAME);
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(2));
    }
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when playlist not found")
  void shouldThrowExceptionWhenPlaylistNotFound() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.findByIdAndUsername(999L, TEST_USERNAME)).thenReturn(Optional.empty());

      // Act & Assert
      EntityNotFoundException exception = assertThrows(
          EntityNotFoundException.class,
          () -> playlistService.getPlaylistById(999L)
      );

      assertEquals("Playlist not found with ID: 999", exception.getMessage());
      verify(playlistRepository, times(1)).findByIdAndUsername(999L, TEST_USERNAME);
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(2));
    }
  }

  @Test
  @DisplayName("Should add song to playlist successfully")
  void shouldAddSongToPlaylist() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.findByIdAndUsername(1L, TEST_USERNAME)).thenReturn(Optional.of(playlist));
      when(songService.getSongById(1L)).thenReturn(song);
      when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);

      // Act
      Playlist updated = playlistService.addSongToPlaylist(1L, 1L);

      // Assert
      assertNotNull(updated);
      assertTrue(playlist.getSongs().contains(song));
      verify(playlistRepository, times(1)).findByIdAndUsername(1L, TEST_USERNAME);
      verify(songService, times(1)).getSongById(1L);
      verify(playlistRepository, times(1)).save(playlist);
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(2));
    }
  }

  @Test
  @DisplayName("Should remove song from playlist successfully")
  void shouldRemoveSongFromPlaylist() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      playlist.addSong(song); // Add song first
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.findByIdAndUsername(1L, TEST_USERNAME)).thenReturn(Optional.of(playlist));
      when(songService.getSongById(1L)).thenReturn(song);
      when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);

      // Act
      playlistService.removeSongFromPlaylist(1L, 1L);

      // Assert
      verify(playlistRepository, times(1)).findByIdAndUsername(1L, TEST_USERNAME);
      verify(songService, times(1)).getSongById(1L);
      verify(playlistRepository, times(1)).save(playlist);
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(2));
    }
  }

  @Test
  @DisplayName("Should delete playlist successfully")
  void shouldDeletePlaylist() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.existsByIdAndName(1L, TEST_USERNAME)).thenReturn(true);

      // Act
      playlistService.deletePlaylist(1L);

      // Assert
      verify(playlistRepository, times(1)).existsByIdAndName(1L, TEST_USERNAME);
      verify(playlistRepository, times(1)).deleteByIdAndName(1L, TEST_USERNAME);
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(2)); // Called twice in the method
    }
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when deleting non-existent playlist")
  void shouldThrowExceptionWhenDeletingNonExistentPlaylist() {
    try (MockedStatic<SecurityContextUtil> mockedSecurity = mockStatic(SecurityContextUtil.class)) {
      // Arrange
      mockedSecurity.when(SecurityContextUtil::getLoggedInUsername).thenReturn(TEST_USERNAME);
      when(playlistRepository.existsByIdAndName(999L, TEST_USERNAME)).thenReturn(false);

      // Act & Assert
      EntityNotFoundException exception = assertThrows(
          EntityNotFoundException.class,
          () -> playlistService.deletePlaylist(999L)
      );

      assertEquals("Playlist not found with ID: 999", exception.getMessage());
      verify(playlistRepository, times(1)).existsByIdAndName(999L, TEST_USERNAME);
      verify(playlistRepository, times(0)).deleteByIdAndName(anyLong(), anyString());
      mockedSecurity.verify(SecurityContextUtil::getLoggedInUsername, times(1));
    }
  }
}
