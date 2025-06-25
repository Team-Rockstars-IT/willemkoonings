package com.wkoonings.rockstarsit.integration;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.model.Song;
import com.wkoonings.rockstarsit.service.ArtistService;
import com.wkoonings.rockstarsit.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class SongServiceIntegrationIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @Autowired
  private SongService songService;

  @Autowired
  private ArtistService artistService;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  @Test
  void shouldCreateAndRetrieveSong() {
    // Given
    Artist artist = new Artist();
    artist.setName("Blue Öyster Cult");

    Artist createdArtist = artistService.createArtist(artist);

    Song song = new Song("Don't Fear the Reaper",
                         1975,
                         createdArtist,
                         "dontfearthereaper",
                         141,
                         322822,
                         "Classic Rock",
                         "5QTxFnGygVM4jFQiBovmRo",
                         "Agents of Fortune");

    // When - Create song
    Song createdSong = songService.createSong(song);

    // Then - Verify creation
    assert createdSong.getId() != null;
    assert createdSong.getName().equals("Don't Fear the Reaper");

    // When - Retrieve song
    Song retrievedSong = songService.getSongById(createdSong.getId());

    // Then - Verify retrieval
    assert retrievedSong != null;
    assert retrievedSong.getName().equals("Don't Fear the Reaper");
  }
}
