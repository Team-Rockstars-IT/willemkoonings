package com.wkoonings.rockstarsit.integration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.persistence.ArtistRepository;
import com.wkoonings.rockstarsit.persistence.SongRepository;
import com.wkoonings.rockstarsit.service.DataInitializationService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class DataInitializationServiceIT {

  @MockitoBean
  private RestTemplate restTemplate;

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @Autowired
  private DataInitializationService dataInitializationService;

  @Autowired
  private ArtistRepository artistRepository;

  @Autowired
  private SongRepository songRepository;

  private String artistsJsonContent;
  private String songsJsonContent;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("app.data.artists-url", () -> "http://localhost:8080/artists.json");
    registry.add("app.data.songs-url", () -> "http://localhost:8080/songs.json");
  }

  @BeforeEach
  void setUp() throws IOException {
    songRepository.deleteAll();
    artistRepository.deleteAll();

    ClassPathResource artistsResource = new ClassPathResource("data/artists.json");
    ClassPathResource songsResource = new ClassPathResource("data/songs.json");

    artistsJsonContent = StreamUtils.copyToString(
        artistsResource.getInputStream(),
        StandardCharsets.UTF_8
    );

    songsJsonContent = StreamUtils.copyToString(
        songsResource.getInputStream(),
        StandardCharsets.UTF_8
    );
  }

  @Test
  @DisplayName("Should initialize data successfully with classpath JSON files")
  void shouldInitializeDataSuccessfullyWithClasspathFiles() {
    // Given
    when(restTemplate.getForObject("http://localhost:8080/artists.json", String.class))
        .thenReturn(artistsJsonContent);
    when(restTemplate.getForObject("http://localhost:8080/songs.json", String.class))
        .thenReturn(songsJsonContent);

    // When
    dataInitializationService.initializeData();

    // Then
    // Verify database state
    List<Artist> savedArtists = artistRepository.findAll();
    List<Song> savedSongs = songRepository.findAll();

    assertThat(savedArtists).isNotEmpty();
    assertThat(savedSongs).isNotEmpty();

    // Verify that songs have valid artist references
    assertThat(savedSongs).allSatisfy(song -> {
      assertThat(song.getArtist()).isNotNull();
      assertThat(song.getArtist().getName()).isNotNull();
    });

    // Verify all artists in songs exist in the artists list
    List<String> artistNames = savedArtists.stream()
                                           .map(Artist::getName)
                                           .toList();

    savedSongs.forEach(song -> {
      assertThat(artistNames).contains(song.getArtist().getName());
    });

    // Verify REST calls were made
    //    verify(restTemplate).getForObject("http://localhost:8080/artists.json", String.class);
    //    verify(restTemplate).getForObject("http://localhost:8080/songs.json", String.class);
  }
}
