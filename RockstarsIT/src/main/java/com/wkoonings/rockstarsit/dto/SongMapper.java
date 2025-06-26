package com.wkoonings.rockstarsit.dto;

import com.wkoonings.rockstarsit.model.Song;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class SongMapper {

  public static SongPageResponse toResponse(Page<Song> page) {
    SongPageResponse response = new SongPageResponse();

    // Convert songs to responses
    List<SongResponse> songResponses = page.getContent()
                                           .stream()
                                           .map(SongMapper::toResponse)
                                           .collect(Collectors.toList());

    response.setContent(songResponses);

    // Create and set page info
    PageInfo pageInfo = new PageInfo();
    pageInfo.setSize(page.getSize());
    pageInfo.setNumber(page.getNumber());
    pageInfo.setTotalElements(page.getTotalElements());
    pageInfo.setTotalPages(page.getTotalPages());
    pageInfo.setFirst(page.isFirst());
    pageInfo.setLast(page.isLast());
    pageInfo.setNumberOfElements(page.getNumberOfElements());
    pageInfo.setEmpty(page.isEmpty());

    response.setPage(pageInfo);

    return response;
  }

  public static SongResponse toResponse(Song song) {
    SongResponse response = new SongResponse();
    response.setId(song.getId());
    response.setName(song.getName());
    response.setYear(song.getYear());
    response.setShortname(song.getShortname());
    response.setArtistId(song.getArtist().getId());
    response.setBpm(song.getBpm());
    response.setDuration(song.getDuration());
    response.setGenre(song.getGenre());
    response.setSpotifyId(song.getSpotifyId());
    response.setAlbum(song.getAlbum());

    return response;
  }

  public static Song fromRequest(AdditSongRequest request) {
    Song song = new Song();
    song.setName(request.getName());
    song.setYear(request.getYear());
    song.setShortname(request.getShortname());
    song.setBpm(request.getBpm());
    song.setDuration(request.getDuration());
    song.setGenre(request.getGenre());
    song.setSpotifyId(request.getSpotifyId());
    return song;
  }
}
