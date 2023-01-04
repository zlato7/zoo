package com.example.Zoo.dto;

import static com.example.Zoo.exception.ExceptionMessages.NOT_EXISTING_GENDER;

import com.example.Zoo.exception.ApiRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
  MALE("male"),
  FEMALE("female");

  private String text;

  Gender(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  @JsonCreator
  public static Gender getGenderFromString(String value) {
    for (Gender gender : Gender.values()) {
      if (value.equalsIgnoreCase(gender.getText())) {
        return gender;
      }
    }
    throw new ApiRequestException(NOT_EXISTING_GENDER);
  }

}