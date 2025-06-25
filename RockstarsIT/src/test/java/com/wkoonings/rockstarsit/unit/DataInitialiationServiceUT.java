package com.wkoonings.rockstarsit.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.service.ArtistService;
import com.wkoonings.rockstarsit.service.DataInitializationService;
import com.wkoonings.rockstarsit.service.DataInitializationService.ArtistDto;
import com.wkoonings.rockstarsit.service.DataInitializationService.SongDto;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializationService Unit Tests")
public class DataInitialiationServiceUT {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ArtistService artistService;

  @InjectMocks
  private DataInitializationService dataInitializationService;

  @Test
  @DisplayName("Should successfully unmarshal JSON to ArtistDto and convert to Artist")
  void shouldProcessArtistData() throws IOException {
    // Given
    String artistJson = """
                        {
                            "Id": 1,
                            "Name": "Test Artist"
                        }
                        """;
    ArtistDto expectedArtistDto = new ArtistDto(1L, "Test Artist");

    when(objectMapper.readValue(artistJson, ArtistDto.class)).thenReturn(expectedArtistDto);

    // When
    ArtistDto unmarshalledDto = objectMapper.readValue(artistJson, ArtistDto.class);
    Artist convertedArtist = dataInitializationService.convertToArtist(unmarshalledDto);

    // Then
    assertNotNull(convertedArtist);
    Assertions.assertEquals("Test Artist", convertedArtist.getName());
    Assertions.assertEquals(1L, convertedArtist.getExternalId());
  }

  @Test
  @DisplayName("Should successfully unmarshal JSON array to List<ArtistDto> and convert to Artists")
  void shouldProcessArtistCollectionData() throws IOException {
    // Given
    String artistsJson = """
                         [
                             {
                                 "Id": 1,
                                 "Name": "Test Artist 1"
                             },
                             {
                                 "Id": 2,
                                 "Name": "Test Artist 2"
                             }
                         ]
                         """;
    ArtistDto[] expectedArtistDtos = new ArtistDto[]{
        new ArtistDto(1L, "Test Artist 1"),
        new ArtistDto(2L, "Test Artist 2")
    };

    when(objectMapper.readValue(artistsJson, ArtistDto[].class)).thenReturn(expectedArtistDtos);

    // When
    ArtistDto[] unmarshalledDtos = objectMapper.readValue(artistsJson, ArtistDto[].class);
    List<Artist> convertedArtists = Arrays.stream(unmarshalledDtos)
                                          .map(dataInitializationService::convertToArtist)
                                          .toList();

    // Then
    assertNotNull(convertedArtists);
    Assertions.assertEquals(2, convertedArtists.size());
    Assertions.assertEquals("Test Artist 1", convertedArtists.get(0).getName());
    Assertions.assertEquals(1L, convertedArtists.get(0).getExternalId());
    Assertions.assertEquals("Test Artist 2", convertedArtists.get(1).getName());
    Assertions.assertEquals(2L, convertedArtists.get(1).getExternalId());
  }

  @Test
  @DisplayName("Should successfully unmarshal JSON to SongDto and convert to Song")
  void shouldProcessSongData() throws IOException {
    // Given
    String songJson = """
                      {
                          "Id": 1
                          "Name": "Test Song",
                          "Year": 2023,
                          "Artist": "Test Artist",
                          "Shortname": "testsong",
                          "Bpm": 120,
                          "Duration": 180000,
                          "Genre": "Rock",
                          "SpotifyId": "spotify123",
                          "Album": "Test Album"
                      }
                      """;
    SongDto expectedSongDto = new SongDto(
        1L, "Test Song", 2023, "Test Artist", "testsong", 120, 180000, "Rock", "spotify123", "Test Album");

    when(objectMapper.readValue(songJson, SongDto.class)).thenReturn(expectedSongDto);
    when(artistService.getArtistByName("Test Artist")).thenReturn(new Artist("Test Artist", List.of()));

    // When
    SongDto unmarshalledDto = objectMapper.readValue(songJson, SongDto.class);
    Song convertedSong = dataInitializationService.convertToSong(unmarshalledDto);

    // Then
    assertNotNull(convertedSong);
    Assertions.assertEquals("Test Song", convertedSong.getName());
    Assertions.assertEquals(2023, convertedSong.getYear());
    Assertions.assertEquals("testsong", convertedSong.getShortname());
    Assertions.assertEquals(120, convertedSong.getBpm());
    Assertions.assertEquals(180000, convertedSong.getDuration());
    Assertions.assertEquals("Rock", convertedSong.getGenre());
    Assertions.assertEquals("spotify123", convertedSong.getSpotifyId());
    Assertions.assertEquals("Test Album", convertedSong.getAlbum());
    Assertions.assertEquals(1L, convertedSong.getExternalId());
  }

  @Test
  @DisplayName("Should successfully unmarshal JSON array to List<SongDto> and convert to Songs")
  void shouldProcessSongCollectionData() throws IOException {
    // Given
    String songsJson = """
                       [
                           {
                               "Id": 1,
                               "Name": "Test Song 1",
                               "Year": 2023,
                               "Artist": "Test Artist",
                               "Shortname": "testsong1",
                               "Bpm": 120,
                               "Duration": 180000,
                               "Genre": "Rock",
                               "SpotifyId": "spotify123",
                               "Album": "Test Album"
                           },
                           {
                               "Id": 2,
                               "Name": "Test Song 2",
                               "Year": 2024,
                               "Artist": "Test Artist",
                               "Shortname": "testsong2",
                               "Bpm": 130,
                               "Duration": 200000,
                               "Genre": "Pop",
                               "SpotifyId": "spotify456",
                               "Album": "Test Album 2"
                           }
                       ]
                       """;
    SongDto[] expectedSongDtos = new SongDto[]{
        new SongDto(1L, "Test Song 1", 2023, "Test Artist", "testsong1", 120, 180000, "Rock", "spotify123", "Test Album"),
        new SongDto(2L, "Test Song 2", 2024, "Test Artist", "testsong2", 130, 200000, "Pop", "spotify456", "Test Album 2")
    };

    when(objectMapper.readValue(songsJson, SongDto[].class)).thenReturn(expectedSongDtos);
    when(artistService.getArtistByName("Test Artist")).thenReturn(new Artist("Test Artist", List.of()));

    // When
    SongDto[] unmarshalledDtos = objectMapper.readValue(songsJson, SongDto[].class);
    List<Song> convertedSongs = Arrays.stream(unmarshalledDtos)
                                      .map(dto -> dataInitializationService.convertToSong(dto))
                                      .toList();

    // Then
    assertNotNull(convertedSongs);
    Assertions.assertEquals(2, convertedSongs.size());

    Song song1 = convertedSongs.get(0);
    Assertions.assertEquals("Test Song 1", song1.getName());
    Assertions.assertEquals(2023, song1.getYear());
    Assertions.assertEquals("testsong1", song1.getShortname());
    Assertions.assertEquals(120, song1.getBpm());
    Assertions.assertEquals(180000, song1.getDuration());
    Assertions.assertEquals("Rock", song1.getGenre());
    Assertions.assertEquals("spotify123", song1.getSpotifyId());
    Assertions.assertEquals("Test Album", song1.getAlbum());
    Assertions.assertEquals(1L, song1.getExternalId());

    Song song2 = convertedSongs.get(1);
    Assertions.assertEquals("Test Song 2", song2.getName());
    Assertions.assertEquals(2024, song2.getYear());
    Assertions.assertEquals("testsong2", song2.getShortname());
    Assertions.assertEquals(130, song2.getBpm());
    Assertions.assertEquals(200000, song2.getDuration());
    Assertions.assertEquals("Pop", song2.getGenre());
    Assertions.assertEquals("spotify456", song2.getSpotifyId());
    Assertions.assertEquals("Test Album 2", song2.getAlbum());
    Assertions.assertEquals(2L, song2.getExternalId());
  }
}
