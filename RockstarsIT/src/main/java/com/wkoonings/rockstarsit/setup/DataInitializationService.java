package com.wkoonings.rockstarsit.setup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.service.ArtistService;
import com.wkoonings.rockstarsit.service.SongService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      this.initializeData();
    }
  }

  public void initializeData() {
    try {
      log.info("Starting data initialization from URLs...");
      log.info("Artists URL: {}", artistsUrl);
      log.info("Songs URL: {}", songsUrl);

      HashMap<String, ArtistDto> artistsRegistry = this.loadArtistsFromUrl();

      HashMap<String, List<Song>> artistSongs = new HashMap<>(artistsRegistry.size());
      artistsRegistry.values().forEach(a -> artistSongs.put(a.getName(), new ArrayList<>()));

      List<SongDto> songs = this.loadSongsFromUrl();
      songs.stream()
           .filter(s -> s.getYear() < 2016 && artistSongs.containsKey(s.getArtistName()))
           .forEach(s -> {
             ArtistDto artistDto = artistsRegistry.get(s.getArtistName());
             if (s.getGenre().equalsIgnoreCase("metal")) {
               artistDto.setMetal(true);
             }
             artistSongs.get(s.getArtistName()).add(this.convertToSong(s));
           });

      final List<Artist> filteredBands =
          artistsRegistry.values().stream().filter(ArtistDto::isMetal)
                         .map(this::convertToArtist).toList();

      // Check which artists already exist and filter out duplicates
      List<Artist> newArtistsToCreate = this.filterNewArtists(filteredBands);

      if (!newArtistsToCreate.isEmpty()) {
        final List<Artist> savedArtists = artistService.createArtists(newArtistsToCreate);
        log.info("Successfully created {} new artists", savedArtists.size());
      } else {
        log.info("No new artists to create - all artists already exist");
      }

      // Get all existing artists (both newly created and previously existing)
      List<Artist> allExistingArtists = this.getAllRelevantArtists(filteredBands);

      final List<Song> filteredSongs = new ArrayList<>();
      allExistingArtists.forEach(a -> {
        List<Song> artistSongList = artistSongs.get(a.getName());
        if (artistSongList != null) {
          filteredSongs.addAll(artistSongList.stream().peek(s -> s.setArtist(a)).toList());
        }
      });

      // Similarly filter songs to avoid duplicates
      List<Song> newSongsToCreate = this.filterNewSongs(filteredSongs);

      if (!newSongsToCreate.isEmpty()) {
        final List<Song> savedSongs = songService.createSongs(newSongsToCreate);
        log.info("Successfully created {} new songs", savedSongs.size());
      } else {
        log.info("No new songs to create - all songs already exist");
      }

      log.info("Data initialization completed successfully!");
    } catch (Exception e) {
      log.error("Failed to initialize data", e);
      throw new RuntimeException("Data initialization failed", e);
    }
  }

  private HashMap<String, ArtistDto> loadArtistsFromUrl() {
    try {
      log.info("Fetching artists from URL: {}", artistsUrl);

      String jsonResponse = restTemplate.getForObject(artistsUrl, String.class);

      if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
        throw new RuntimeException("Empty response from artists URL");
      }

      ArtistDto[] artistDtos = objectMapper.readValue(jsonResponse, ArtistDto[].class);

      log.info("Successfully fetched {} artist records", artistDtos.length);

      return Arrays.stream(artistDtos)
                   .collect(Collectors.toMap(ArtistDto::getName, a -> a, (a1, a2) -> a1, HashMap::new));
    } catch (Exception e) {
      log.error("Failed to load artists from URL: {}", artistsUrl, e);
      throw new RuntimeException("Failed to fetch artists data", e);
    }
  }

  private List<SongDto> loadSongsFromUrl() {
    try {
      log.info("Fetching songs from URL: {}", songsUrl);
      String jsonResponse = restTemplate.getForObject(songsUrl, String.class);

      if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
        throw new RuntimeException("Empty response from songs URL");
      }

      SongDto[] songDtos = objectMapper.readValue(jsonResponse, SongDto[].class);

      log.info("Successfully fetched {} song records", songDtos.length);

      return Arrays.stream(songDtos).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to load songs from URL: {}", songsUrl, e);
      throw new RuntimeException("Failed to fetch songs data", e);
    }
  }

  private List<Artist> filterNewArtists(List<Artist> artistsToCheck) {
    // Get all external IDs from the artists we want to create
    List<Long> externalIds = artistsToCheck.stream()
                                           .map(Artist::getExternalId)
                                           .toList();

    // Find which external IDs already exist in the database
    List<Long> existingExternalIds = artistService.findExternalIdsByExternalIds(externalIds);

    // Filter out artists whose external IDs already exist
    List<Artist> newArtists = artistsToCheck.stream()
                                            .filter(artist -> !existingExternalIds.contains(artist.getExternalId()))
                                            .toList();

    log.info("Found {} existing artists, {} new artists to create",
             existingExternalIds.size(), newArtists.size());

    return newArtists;
  }

  private List<Artist> getAllRelevantArtists(List<Artist> filteredBands) {
    // Get all external IDs
    List<Long> externalIds = filteredBands.stream()
                                          .map(Artist::getExternalId)
                                          .toList();

    // Fetch all existing artists with these external IDs
    return artistService.findByExternalIds(externalIds);
  }

  private List<Song> filterNewSongs(List<Song> songsToCheck) {
    // Get all external IDs from the songs we want to create
    List<Long> externalIds = songsToCheck.stream()
                                         .map(Song::getExternalId)
                                         .toList();

    // Find which external IDs already exist in the database
    List<Long> existingSongExternalIds = songService.findExternalIdsByExternalIds(externalIds);

    // Filter out songs whose external IDs already exist
    List<Song> newSongs = songsToCheck.stream()
                                      .filter(song -> !existingSongExternalIds.contains(song.getExternalId()))
                                      .toList();

    log.info("Found {} existing songs, {} new songs to create",
             existingSongExternalIds.size(), newSongs.size());

    return newSongs;
  }

  public Artist convertToArtist(ArtistDto dto) {
    return Artist.builder()
                 .name(dto.getName())
                 .externalId(dto.getId())
                 .build();
  }

  public Song convertToSong(SongDto dto) {
    return Song.builder()
               .name(dto.getName())
               .year(dto.getYear())
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
    private boolean isMetal = false;
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
