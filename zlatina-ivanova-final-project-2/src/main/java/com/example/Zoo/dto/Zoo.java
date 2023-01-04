package com.example.Zoo.dto;

import java.math.BigDecimal;

public class Zoo {

  private Long zooId;
  private BigDecimal money;
  private Integer availablePlaces;

  public Long getZooId() {
    return zooId;
  }

  public BigDecimal getMoney() {
    return money;
  }

  public Integer getAvailablePlaces() {
    return availablePlaces;
  }

}
