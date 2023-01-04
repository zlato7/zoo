package com.example.Zoo.dao;

import com.example.Zoo.dto.AnimalRequest;
import java.math.BigDecimal;
import java.util.Optional;

public interface ZooDao {

  void decreaseFreePlaces(Long zooId);

  Optional<Integer> getFreePlaces(Long zooId);

  void decreaseMoney(Long zooId, AnimalRequest animalRequest);

  BigDecimal money(Long zooId);

  void increaseMoney(Long zooId, BigDecimal price);

  void increaseFreePlaces(Long zooId);
}
