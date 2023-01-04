package com.example.Zoo.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(value = {ApiRequestException.class})
  public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {

    ErrorDto apiException = new ErrorDto(
        e.getMessage(),
        ZonedDateTime.now(ZoneId.of("Z"))
    );

    return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  public ResponseEntity<Object> handle(MethodArgumentNotValidException e) {


    return new ResponseEntity<>(e.getBindingResult().getFieldErrors().stream().map(
        DefaultMessageSourceResolvable::getDefaultMessage).collect(
        Collectors.toList()), HttpStatus.BAD_REQUEST);
  }
}
