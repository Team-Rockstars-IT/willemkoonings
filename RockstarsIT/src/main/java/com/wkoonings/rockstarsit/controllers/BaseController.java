package com.wkoonings.rockstarsit.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class BaseController {

  protected Pageable getPageableFor(int page, int size, String sort) {
    Sort.Direction direction = Sort.Direction.ASC;
    String property = "id";

    if (sort.contains(",")) {
      String[] sortParts = sort.split(",");
      property = sortParts[0];
      direction = sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    return PageRequest.of(page, size, Sort.by(direction, property));
  }
}
