package com.example.Zoo.dto;

import java.math.BigDecimal;

public class Animal {

  private Long animalId;
  private String kind;
  private Diet diet;
  private Gender gender;
  private Color color;
  private BigDecimal price;
  public Long zooId;

  public Long getAnimalId() {
    return animalId;
  }

  public String getKind() {
    return kind;
  }

  public Diet getDiet() {
    return diet;
  }

  public Gender getGender() {
    return gender;
  }

  public Color getColor() {
    return color;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setAnimalId(Long animalId) {
    this.animalId = animalId;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public void setDiet(Diet diet) {
    this.diet = diet;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setZooId(Long zooId) {
    this.zooId = zooId;
  }

  public Long getZooId() {
    return zooId;
  }

}