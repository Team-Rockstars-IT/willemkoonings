package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.dto.DTOArtist;
import com.wkoonings.rockstarsit.dto.DTOArtistsResponse;
import com.wkoonings.rockstarsit.model.Artist;
import java.util.List;

public class ArtistMapper {

  public static DTOArtistsResponse toDTOArtistsResponse(List<Artist> artists) {
    DTOArtistsResponse response = new DTOArtistsResponse();
    response.content(artists.stream().map(ArtistMapper::toDTOArtist).toList());
    return response;
  }

  public static DTOArtist toDTOArtist(Artist artist) {
    DTOArtist dtoArtist = new DTOArtist();
    dtoArtist.setId(artist.getId());
    dtoArtist.setName(artist.getName());
    dtoArtist.setSongs(artist.getSongs().stream().map(SongMapper::toDTOSong).toList());
    return dtoArtist;
  }

  public static Artist fromDTOArtist(DTOArtist dtoArtist) {
    Artist artist = new Artist();
    artist.setId(dtoArtist.getId());
    artist.setName(dtoArtist.getName());
    return artist;
  }
}
