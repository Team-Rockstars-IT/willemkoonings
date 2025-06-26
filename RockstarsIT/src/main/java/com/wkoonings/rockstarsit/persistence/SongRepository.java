package com.wkoonings.rockstarsit.persistence;

import com.wkoonings.rockstarsit.model.Song;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

  @Query("SELECT s.externalId FROM Song s WHERE s.externalId IN :externalIds")
  List<Long> findExternalIdsByExternalIdIn(@Param("externalIds") List<Long> externalIds);

  List<Song> findByExternalIdIn(List<Long> externalIds);
}
