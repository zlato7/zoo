package com.example.Zoo.service;

import com.example.Zoo.dto.Animal;
import com.example.Zoo.dto.AnimalRequest;
import java.util.List;

public interface AnimalService {

  void buyAnimal(Long zooId, AnimalRequest animalRequest) ;

  List<Animal> getAllAnimals(Long zooId);

  void paintAnimal(Long animalId, String color);

  void sellAnimal(Long animalId);

  List<Animal> showSortedAnimalsPerParam(Integer offset, Integer size,String color,String gender, String parameter);
}
