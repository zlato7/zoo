package com.example.Zoo.dto;

import static com.example.Zoo.exception.ExceptionMessages.NOT_EXISTING_DIET;

import com.example.Zoo.exception.ApiRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Diet {
  HERBIVORE("herbivore"),
  PREDATOR("predator");

  private String text;

  Diet(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  @JsonCreator
  public static Diet getDietFromString(String value){
    for (Diet diet:Diet.values()) {
      if (diet.getText().equalsIgnoreCase(value)){
        return diet;
      }
    }
    throw new ApiRequestException(NOT_EXISTING_DIET);
  }

}
