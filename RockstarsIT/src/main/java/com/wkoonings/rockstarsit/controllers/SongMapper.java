package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.dto.DTOSong;
import com.wkoonings.rockstarsit.dto.DTOSongsResponse;
import com.wkoonings.rockstarsit.model.Song;
import java.util.List;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class SongMapper {

  public static DTOSongsResponse toDTOSongsResponse(List<Song> songs) {
    DTOSongsResponse response = new DTOSongsResponse();
    response.content(songs.stream().map(SongMapper::toDTOSong).toList());
    return response;
  }

  public static DTOSong toDTOSong(Song song) {
    DTOSong dtoSong = new DTOSong();
    dtoSong.setId(song.getId());
    dtoSong.setName(song.getName());
    dtoSong.setYear(song.getYear());
    dtoSong.setArtistName(song.getArtist().getName());
    dtoSong.setArtistLink(String.valueOf(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ArtistController.class)
                                                                                   .getArtistById(song.getArtist().getId())).withRel("artist")));
    dtoSong.setShortname(song.getShortname());
    dtoSong.setBpm(song.getBpm());
    dtoSong.setDuration(song.getDuration());
    dtoSong.setGenre(song.getGenre());
    dtoSong.setSpotifyId(song.getSpotifyId());
    dtoSong.setAlbum(song.getAlbum());

    return dtoSong;
  }

  public static Song fromDTOSong(DTOSong dtoSong) {
    Song song = new Song();
    song.setId(dtoSong.getId());
    song.setName(dtoSong.getName());
    song.setYear(dtoSong.getYear());
    song.setShortname(dtoSong.getShortname());
    song.setBpm(dtoSong.getBpm());
    song.setDuration(dtoSong.getDuration());
    song.setGenre(dtoSong.getGenre());
    song.setSpotifyId(dtoSong.getSpotifyId());
    return song;
  }
}
