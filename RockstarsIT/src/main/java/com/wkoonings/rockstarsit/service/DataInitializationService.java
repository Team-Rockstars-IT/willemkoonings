package com.wkoonings.rockstarsit.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService {

  private final ArtistService artistService;
  private final SongService songService;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;

  @Value("${app.data.artists-url}")
  private String artistsUrl;

  @Value("${app.data.songs-url}")
  private String songsUrl;

  @Value("${app.data.init.enabled:true}")
  private boolean initEnabled;

  private final Map<String, Artist> artistMap = new HashMap<>();

  @EventListener(ApplicationReadyEvent.class)
  public void initializeDataOnApplicationReady() {
    if (initEnabled) {
      log.info("Application is ready, starting data initialization...");
      initializeData();
    }
  }

  public void initializeData() {
    try {
      log.info("Starting data initialization from URLs...");
      log.info("Artists URL: {}", artistsUrl);
      log.info("Songs URL: {}", songsUrl);

      // Load and process artists first
      List<Artist> artists = this.loadArtistsFromUrl();
      List<Artist> savedArtists = artistService.createArtists(artists);
      log.info("Successfully loaded {} artists", savedArtists.size());

      this.artistMap.putAll(savedArtists.stream().collect(Collectors.toMap(Artist::getName, a -> a)));

      // Load and process songs
      List<Song> songs = this.loadSongsFromUrl();
      List<Song> savedSongs = songService.createSongs(songs);
      log.info("Successfully loaded {} songs", savedSongs.size());

      log.info("Data initialization completed successfully!");
    } catch (Exception e) {
      log.error("Failed to initialize data", e);
      throw new RuntimeException("Data initialization failed", e);
    }
  }

  private List<Artist> loadArtistsFromUrl() {
    try {
      log.info("Fetching artists from URL: {}", artistsUrl);

      String jsonResponse = restTemplate.getForObject(artistsUrl, String.class);

      if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
        throw new RuntimeException("Empty response from artists URL");
      }

      ArtistDto[] artistDtos = objectMapper.readValue(jsonResponse, ArtistDto[].class);

      log.info("Successfully fetched {} artist records", artistDtos.length);

      return Arrays.stream(artistDtos)
                   .map(this::convertToArtist)
                   .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to load artists from URL: {}", artistsUrl, e);
      throw new RuntimeException("Failed to fetch artists data", e);
    }
  }

  private List<Song> loadSongsFromUrl() {
    try {
      log.info("Fetching songs from URL: {}", songsUrl);
      String jsonResponse = restTemplate.getForObject(songsUrl, String.class);

      if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
        throw new RuntimeException("Empty response from songs URL");
      }

      SongDto[] songDtos = objectMapper.readValue(jsonResponse, SongDto[].class);

      log.info("Successfully fetched {} song records", songDtos.length);

      return Arrays.stream(songDtos)
                   .map(this::convertToSong)
                   .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to load songs from URL: {}", songsUrl, e);
      throw new RuntimeException("Failed to fetch songs data", e);
    }
  }

  public Artist convertToArtist(ArtistDto dto) {
    return Artist.builder()
                 .name(dto.getName())
                 .externalId(dto.getId())
                 .build();
  }

  public Song convertToSong(SongDto dto) {
    Artist artist = this.artistMap.get(dto.getArtistName());

    if (Objects.isNull(artist)) {
      try {
        artist = this.artistService.getArtistByName(dto.getArtistName());
      } catch (EntityNotFoundException ex) {
        log.warn("Artist not found for song '{}': {}", dto.getName(), dto.getArtistName());
        artist = this.artistService.createArtist(Artist.builder().name(dto.getArtistName()).build());
      }
    }

    return Song.builder()
               .name(dto.getName())
               .year(dto.getYear())
               .artist(artist)
               .shortname(dto.getShortname())
               .bpm(dto.getBpm())
               .duration(dto.getDuration())
               .genre(dto.getGenre())
               .spotifyId(dto.getSpotifyId())
               .album(dto.getAlbum())
               .externalId(dto.getId())
               .build();
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ArtistDto {

    @JsonProperty("Id")
    private Long id;
    @JsonProperty("Name")
    private String name;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SongDto {

    @JsonProperty("Id")
    private Long id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Year")
    private int year;
    @JsonProperty("Artist")
    private String artistName;
    @JsonProperty("Shortname")
    private String shortname;
    @JsonProperty("Bpm")
    private int bpm;
    @JsonProperty("Duration")
    private int duration;
    @JsonProperty("Genre")
    private String genre;
    @JsonProperty("SpotifyId")
    private String spotifyId;
    @JsonProperty("Album")
    private String album;
  }
}
