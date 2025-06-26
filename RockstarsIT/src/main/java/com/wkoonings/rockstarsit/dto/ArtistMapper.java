package com.wkoonings.rockstarsit.dto;

import com.wkoonings.rockstarsit.model.Artist;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class ArtistMapper {

  public static ArtistPageResponse toResponse(Page<Artist> page) {
    ArtistPageResponse response = new ArtistPageResponse();

    // Convert artists to responses
    List<ArtistResponse> artistResponses = page.getContent()
                                               .stream()
                                               .map(ArtistMapper::toResponse)
                                               .collect(Collectors.toList());

    response.setContent(artistResponses);

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

  public static ArtistResponse toResponse(Artist artist) {
    ArtistResponse response = new ArtistResponse();
    response.setId(artist.getId());
    response.setName(artist.getName());
    return response;
  }

  public static Artist fromRequest(AdditArtistRequest request) {
    Artist artist = new Artist();
    artist.setName(request.getName());
    return artist;
  }
}
