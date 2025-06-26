package com.wkoonings.rockstarsit.persistence;

import com.wkoonings.rockstarsit.model.Playlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

  Optional<Playlist> findByIdAndUsername(Long id, String username);

  List<Playlist> findAllByUsername(String username);

  Page<Playlist> findAllByUsername(String username, Pageable pageable);

  void deleteByIdAndName(Long id, String name);

  boolean existsByIdAndName(Long id, String name);
}
