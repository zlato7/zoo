package com.example.Zoo.dto;

public enum Color {
  NO_COLOR,
  WHITE,
  YELLOW,
  BLUE,
  RED,
  GREEN,
  BLACK,
  BROWN,
  PURPLE,
  GRAY,
  ORANGE,
  PINK;


  public static boolean isValid(String color) {

    for (Color c : Color.values()) {
      if (c.name().equalsIgnoreCase(color)) {
        return true;
      }
    }
    return false;
  }

}
