package com.wkoonings.rockstarsit.dto;

import com.wkoonings.rockstarsit.model.Playlist;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class PlaylistMapper {

  public static PlaylistPageResponse toResponse(Page<Playlist> page) {
    PlaylistPageResponse response = new PlaylistPageResponse();

    List<PlaylistResponse> playlistResponses = page.getContent()
                                                   .stream()
                                                   .map(p -> PlaylistMapper.toResponse(p, false))
                                                   .collect(Collectors.toList());

    response.setContent(playlistResponses);

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

  public static PlaylistResponse toResponse(Playlist playlist, boolean includeSongs) {
    PlaylistResponse response = new PlaylistResponse();
    response.setId(playlist.getId());
    response.setName(playlist.getName());

    if (includeSongs && playlist.getSongs() != null) {
      List<SongResponse> songResponses = playlist.getSongs()
                                                 .stream()
                                                 .map(SongMapper::toResponse)
                                                 .collect(Collectors.toList());
      response.setSongs(songResponses);
    }

    return response;
  }

  public static Playlist fromCreateRequest(CreatePlaylistRequest request, String username) {
    return new Playlist(request.getName(), username);
  }
}
