package com.wkoonings.rockstarsit.persistence;

import com.wkoonings.rockstarsit.model.Artist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

  Optional<Artist> findByName(String name);

  @Query("SELECT a.externalId FROM Artist a WHERE a.externalId IN :externalIds")
  List<Long> findExternalIdsByExternalIdIn(@Param("externalIds") List<Long> externalIds);

  List<Artist> findByExternalIdIn(List<Long> externalIds);
}
