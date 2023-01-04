package com.example.Zoo.dao;

import com.example.Zoo.dto.Animal;
import com.example.Zoo.dto.AnimalRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

public interface AnimalDao {

  void buyAnimal(Long zooId, AnimalRequest animalRequest);

  Optional<Animal> getAnimal(Long id);

  List<Animal> getAllAnimals(Long zooId);

  List<Animal> getAllAnimalsByKind(Long zooId, String kind);

  int getCountOfAllAnimals(Long zooId);

  void paintAnimal(Long animalId, String color);

  List<Animal> getAllAnimalsPerColorInTheZoo(Long zooId, String color);

  void sellAnimal(Long animalId);

  List<Animal> showSortedAnimalsPerParam(PageRequest pageRequest, String color, String gender, String parameter);
}
