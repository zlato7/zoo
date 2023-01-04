package com.example.Zoo.exception;

public class ApiRequestException extends RuntimeException{

  public ApiRequestException(String message) {
    super(message);
  }

}
