package com.example.Zoo.exception;

import java.time.ZonedDateTime;

public class ErrorDto {

  private final String message;
  private final ZonedDateTime timestamp;

  public ErrorDto(String message, ZonedDateTime timestamp) {
    this.message = message;
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }
}
