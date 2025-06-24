package com.wkoonings.rockstarsit.exception;

import com.wkoonings.rockstarsit.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
    log.error("Entity not found: {}", exception.getMessage());

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setTimestamp(OffsetDateTime.now());
    errorResponse.setStatus(404);
    errorResponse.setError("Not Found");
    errorResponse.setMessage(exception.getMessage());

    return errorResponse;
  }
}
