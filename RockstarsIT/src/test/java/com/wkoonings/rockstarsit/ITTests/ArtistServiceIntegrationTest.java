package com.wkoonings.rockstarsit.ITTests;

import com.wkoonings.rockstarsit.model.Artist;
import com.wkoonings.rockstarsit.service.ArtistService;
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
public class ArtistServiceIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

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
  void shouldCreateAndRetrieveArtist() {
    // Given
    Artist artist = new Artist();
    artist.setName("Blue Öyster Cult");

    // When - Create artist
    Artist createdArtist = artistService.createArtist(artist);

    // Then - Verify creation
    assert createdArtist.getId() != null;
    assert createdArtist.getName().equals("Blue Öyster Cult");

    // When - Retrieve artist
    Artist retrievedArtist = artistService.getArtistById(createdArtist.getId());

    // Then - Verify retrieval
    assert retrievedArtist != null;
    assert retrievedArtist.getName().equals("Blue Öyster Cult");
  }
}
