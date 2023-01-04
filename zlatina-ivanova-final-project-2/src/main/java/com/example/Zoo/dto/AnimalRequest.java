package com.example.Zoo.dto;

import static com.example.Zoo.exception.ExceptionMessages.INVALID_TEXT_LENGHT;

import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AnimalRequest {

@NotEmpty
@Size(min = 2, max = 100, message = INVALID_TEXT_LENGHT)
  private String kind;
  @NotNull
  private Diet diet;
  @NotNull
  private Gender gender;
  @Min(value = 1)
  @Max(value = 1000)
  @NotNull
  private BigDecimal price;

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind.trim();
  }

  public Diet getDiet() {
    return diet;
  }

  public Gender getGender() {
    return gender;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setDiet(Diet diet) {
    this.diet = diet;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }
}
