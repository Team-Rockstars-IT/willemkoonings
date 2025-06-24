package com.wkoonings.rockstarsit.persistence;

import com.wkoonings.rockstarsit.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

}
