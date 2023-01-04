package com.example.Zoo.controller;

import static com.example.Zoo.common.Urls.ANIMAL;
import static com.example.Zoo.common.Urls.BUY_OR_SHOW_ANIMALS;
import static com.example.Zoo.common.Urls.SORTED_ANIMALS;

import com.example.Zoo.dto.Animal;
import com.example.Zoo.dto.AnimalRequest;
import com.example.Zoo.service.AnimalService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AnimalController {

  private final AnimalService animalService;

  public AnimalController(AnimalService animalService) {
    this.animalService = animalService;
  }

  @PostMapping(BUY_OR_SHOW_ANIMALS)
  @ResponseStatus(HttpStatus.CREATED)
  public void buyAnimal(@PathVariable Long zooId, @Valid @RequestBody AnimalRequest animalRequest) {
    animalService.buyAnimal(zooId, animalRequest);
  }

  @GetMapping(BUY_OR_SHOW_ANIMALS)
  @ResponseStatus(HttpStatus.OK)
  public List<Animal> getAllAnimals(@PathVariable Long zooId) {
    return animalService.getAllAnimals(zooId);
  }

  @PatchMapping(ANIMAL)
  @ResponseStatus(HttpStatus.OK)
  public void paintAnimal(@PathVariable Long animalId, @RequestParam String color) {
    animalService.paintAnimal(animalId, color);
  }

  @DeleteMapping(ANIMAL)
  @ResponseStatus(HttpStatus.RESET_CONTENT)
  public void sellAnimal(@PathVariable Long animalId) {
    animalService.sellAnimal(animalId);
  }

  @GetMapping(SORTED_ANIMALS)
  @ResponseStatus(HttpStatus.OK)
  public List<Animal> showSortedAnimalsPerParam(@RequestParam Integer offset,
      @RequestParam Integer size, @RequestParam( required = false) String color,
      @RequestParam(required=false) String gender, @RequestParam String parameter) {

    return animalService.showSortedAnimalsPerParam(offset, size, color, gender, parameter);
  }
}