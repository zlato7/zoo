package com.example.Zoo.service;

import static com.example.Zoo.exception.ExceptionMessages.CANT_PAINT_PINK;
import static com.example.Zoo.exception.ExceptionMessages.INVALID_PARAMETER;
import static com.example.Zoo.exception.ExceptionMessages.MORE_THAN_ONE_COUPLE_FORBIDDEN;
import static com.example.Zoo.exception.ExceptionMessages.NOT_ENOUGH_MONEY;
import static com.example.Zoo.exception.ExceptionMessages.REACHED_MAXIMUM_KINDS;
import static com.example.Zoo.exception.ExceptionMessages.SELL_PAINTED_ANIMALS_FORBIDDEN;
import static com.example.Zoo.exception.ExceptionMessages.THE_ZOO_IS_FULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.Zoo.dao.AnimalDao;
import com.example.Zoo.dao.ZooDao;
import com.example.Zoo.dto.Animal;
import com.example.Zoo.dto.AnimalRequest;
import com.example.Zoo.dto.Color;
import com.example.Zoo.dto.Diet;
import com.example.Zoo.dto.Gender;
import com.example.Zoo.dto.Zoo;
import com.example.Zoo.exception.ApiRequestException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AnimalServiceTests {

  @Mock
  private AnimalDao animalDao;
  @Mock
  private ZooDao zooDao;
  private AnimalServiceImpl animalService;
  private AutoCloseable autoCloseable;


  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    animalService = new AnimalServiceImpl(animalDao, zooDao);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  // getAllAnimals

  @Test
  void get_all_animals_test() {
    Zoo zoo = new Zoo();
    animalService.getAllAnimals(zoo.getZooId());
    verify(animalDao).getAllAnimals(zoo.getZooId());
  }

  // paintAnimal

  @Test
  void painting_animal_in_yellow_allowed_test() {
    Animal animal = new Animal();
    animal.setDiet(Diet.PREDATOR);
    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));
    animalService.paintAnimal(animal.getAnimalId(), "yellow");
    verify(animalDao).paintAnimal(animal.getAnimalId(), "yellow");
  }

  @Test
  void painting_animal_in_yellow_forbidden_test() {
    Animal animal = new Animal();
    animal.setDiet(Diet.HERBIVORE);
    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.paintAnimal(animal.getAnimalId(), "yellow"));

    assertEquals("You can't paint herbivore animals in yellow!", exception.getMessage());
  }

  @Test
  void painting_animal_in_valid_color_allowed_test() {
    Animal animal = new Animal();
    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));
    animalService.paintAnimal(animal.getAnimalId(), "red");
    verify(animalDao).paintAnimal(animal.getAnimalId(), "red");
  }

  @Test
  void painting_animal_in_invalid_color_forbidden_test() {
    Animal animal = new Animal();
    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.paintAnimal(animal.getAnimalId(), "invalid color"));

    assertEquals(INVALID_PARAMETER, exception.getMessage());
  }

  @Test
  void painting_animal_in_pink_forbidden_when_predator_test() {
    Animal animal = new Animal();
    animal.setDiet(Diet.PREDATOR);
    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.paintAnimal(animal.getAnimalId(), "pink"));

    assertEquals("You can't paint predator animals in pink!", exception.getMessage());
  }

  @Test
  void painting_animal_in_pink_valid_test() {
    Animal animal = new Animal();
    animal.setDiet(Diet.HERBIVORE);

    List<Animal> animals = new ArrayList<>();

    Animal animal1 = new Animal();
    animals.add(animal1);

    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));
    when(animalDao.getAllAnimalsPerColorInTheZoo(animal.getZooId(), "pink")).thenReturn(
        new ArrayList<>(animals));

    animalService.paintAnimal(animal.getAnimalId(), "pink");
    verify(animalDao).paintAnimal(animal.getAnimalId(), "pink");
  }

  @Test
  void painting_animal_in_pink_forbidden_when_pink_are_already_two_test() {
    Animal animal = new Animal();
    animal.setDiet(Diet.HERBIVORE);

    List<Animal> animals = new ArrayList<>();

    Animal animal1 = new Animal();
    Animal animal2 = new Animal();

    animals.add(animal1);
    animals.add(animal2);

    when(animalDao.getAnimal(animal.getZooId())).thenReturn(Optional.of(animal));
    when(animalDao.getAllAnimalsPerColorInTheZoo(animal.getZooId(), "pink"))
        .thenReturn(animals);

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.paintAnimal(animal.getAnimalId(), "pink"));

    assertEquals(CANT_PAINT_PINK, exception.getMessage());
  }

  // sellAnimal

  @Test
  void sell_lonely_male_animal_which_is_not_painted_allowed_test() {
    Animal animal = new Animal();
    animal.setKind("monkey");
    animal.setGender(Gender.MALE);
    animal.setColor(Color.NO_COLOR);

    List<Animal> animals = new ArrayList<>();
    animals.add(animal);

    when(animalDao.getAnimal(animal.getAnimalId())).thenReturn(Optional.of(animal));
    when(animalDao.getAllAnimalsByKind(animal.getZooId(), animal.getKind())).thenReturn(animals);

    animalService.sellAnimal(animal.getAnimalId());

    verify(animalDao).sellAnimal(animal.getAnimalId());
    verify(zooDao).increaseMoney(animal.getZooId(), animal.getPrice());
  }

  @Test
  void sell_animal_and_increase_free_places_valid_test() {
    Animal animal = new Animal();
    animal.setKind("monkey");
    animal.setGender(Gender.MALE);
    animal.setColor(Color.NO_COLOR);

    when(animalDao.getAnimal(animal.getAnimalId())).thenReturn(Optional.of(animal));
    when(animalDao.getAllAnimalsByKind(animal.getZooId(), animal.getKind())).thenReturn(
        new ArrayList<>());

    animalService.sellAnimal(animal.getAnimalId());

    verify(animalDao).sellAnimal(animal.getAnimalId());
    verify(zooDao).increaseMoney(animal.getZooId(), animal.getPrice());
    verify(zooDao).increaseFreePlaces(animal.getZooId());
  }


  @Test
  void sell_male_animal_which_is_not_painted_forbidden_test() {
    Animal animal = new Animal();
    animal.setKind("monkey");
    animal.setGender(Gender.MALE);
    animal.setColor(Color.NO_COLOR);

    animal.setZooId(1L);

    List<Animal> animals = new ArrayList<>();

    Animal femaleAnimal = new Animal();
    femaleAnimal.setKind(animal.getKind());
    femaleAnimal.setGender(Gender.FEMALE);

    animals.add(animal);
    animals.add(femaleAnimal);

    when(animalDao.getAnimal(animal.getAnimalId())).thenReturn(Optional.of(animal));
    when(animalDao.getAllAnimalsByKind(animal.getZooId(), animal.getKind())).thenReturn(animals);

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.sellAnimal(animal.getAnimalId()));

    assertEquals("You can't leave the female monkey to stay alone!", exception.getMessage());
  }

  @Test
  void sell_female_animal_which_is_not_painted_allowed_test() {
    Animal animal = new Animal();
    animal.setKind("monkey");
    animal.setGender(Gender.FEMALE);
    animal.setColor(Color.NO_COLOR);

    List<Animal> animals = new ArrayList<>();
    animals.add(animal);

    Animal maleAnimal = new Animal();
    maleAnimal.setKind(animal.getKind());
    maleAnimal.setGender(Gender.FEMALE);
    animals.add(maleAnimal);

    when(animalDao.getAnimal(animal.getAnimalId())).thenReturn(Optional.of(animal));
    when(animalDao.getAllAnimalsByKind(animal.getZooId(), animal.getKind())).thenReturn(animals);

    animalService.sellAnimal(animal.getAnimalId());
    verify(animalDao).sellAnimal(animal.getAnimalId());
    verify(zooDao).increaseMoney(animal.getZooId(), animal.getPrice());
  }

  @Test
  void sell_painted_animal_forbidden_test() {
    Animal animal = new Animal();
    animal.setKind("monkey");
    animal.setColor(Color.GREEN);

    when(animalDao.getAnimal(animal.getAnimalId())).thenReturn(Optional.of(animal));

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.sellAnimal(animal.getAnimalId()));

    assertEquals(SELL_PAINTED_ANIMALS_FORBIDDEN, exception.getMessage());
  }

  // buyAnimal

  @Test
  void buy_animal_allowed_test() {
    AnimalRequest animal = new AnimalRequest();
    Zoo zoo = new Zoo();

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(0);
    when(animalDao.getAllAnimalsByKind(zoo.getZooId(), animal.getKind())).thenReturn(
        new ArrayList<>());
    when(zooDao.getFreePlaces(zoo.getZooId())).thenReturn(Optional.of(10));
    when(zooDao.money(zoo.getZooId())).thenReturn(new BigDecimal("5000"));

    animalService.buyAnimal(zoo.getZooId(), animal);

    verify(zooDao).decreaseFreePlaces(zoo.getZooId());
    verify(animalDao).buyAnimal(zoo.getZooId(), animal);
    verify(zooDao).decreaseMoney(zoo.getZooId(), animal);
    verify(zooDao).money(zoo.getZooId());
  }

  @Test
  void buy_animal_when_one_per_kind_with_different_gender_already_exist_allowed_test() {
    AnimalRequest animal = new AnimalRequest();
    animal.setGender(Gender.MALE);
    Zoo zoo = new Zoo();

    List<Animal> animals = new ArrayList<>();
    Animal femaleAnimal = new Animal();
    femaleAnimal.setGender(Gender.FEMALE);
    femaleAnimal.setKind(animal.getKind());
    animals.add(femaleAnimal);

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(1);
    when(animalDao.getAllAnimalsByKind(zoo.getZooId(), animal.getKind())).thenReturn(animals);
    when(zooDao.getFreePlaces(zoo.getZooId())).thenReturn(Optional.of(9));
    when(zooDao.money(zoo.getZooId())).thenReturn(new BigDecimal("4500"));

    animalService.buyAnimal(zoo.getZooId(), animal);

    verify(animalDao).buyAnimal(zoo.getZooId(), animal);
    verify(zooDao).decreaseMoney(zoo.getZooId(), animal);
    verify(zooDao).money(zoo.getZooId());

  }

  @Test
  void buy_animal_is_forbidden_if_has_the_same_animal() {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("monkey");
    animal.setGender(Gender.MALE);
    Zoo zoo = new Zoo();

    List<Animal> animals = new ArrayList<>();

    Animal alreadyExistingAnimal = new Animal();
    alreadyExistingAnimal.setKind(animal.getKind());
    alreadyExistingAnimal.setGender(animal.getGender());

    animals.add(alreadyExistingAnimal);

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(1);
    when(animalDao.getAllAnimalsByKind(zoo.getZooId(), animal.getKind())).thenReturn(animals);

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.buyAnimal(zoo.getZooId(), animal));

    assertEquals("You already have male monkey", exception.getMessage());
  }

  @Test
  void buy_animals_is_forbidden_if_have_maximum_number_of_animals_already_test() {
    AnimalRequest animal = new AnimalRequest();
    Zoo zoo = new Zoo();

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(20);

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.buyAnimal(zoo.getZooId(), animal));

    assertEquals(THE_ZOO_IS_FULL, exception.getMessage());
  }

  @Test
  void buy_animals_is_forbidden_if_have_two_animals_from_the_same_kind_already_test() {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("monkey");

    Zoo zoo = new Zoo();

    List<Animal> animals = new ArrayList<>();

    Animal male = new Animal();
    male.setKind("monkey");
    male.setGender(Gender.MALE);

    Animal female = new Animal();
    female.setKind("monkey");
    female.setGender(Gender.FEMALE);

    animals.add(male);
    animals.add(female);

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(10);
    when(animalDao.getAllAnimalsByKind(zoo.getZooId(), "monkey")).thenReturn(animals);

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.buyAnimal(zoo.getZooId(), animal));

    assertEquals(MORE_THAN_ONE_COUPLE_FORBIDDEN, exception.getMessage());
  }

  @Test
  void buy_animals_is_forbidden_if_no_places_available_test() {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("monkey");
    Zoo zoo = new Zoo();

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(10);
    when(animalDao.getAllAnimalsByKind(zoo.getZooId(), animal.getKind())).thenReturn(
        new ArrayList<>());
    when(zooDao.getFreePlaces(zoo.getZooId())).thenReturn(Optional.of(0));

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.buyAnimal(zoo.getZooId(), animal));

    assertEquals(REACHED_MAXIMUM_KINDS, exception.getMessage());
  }

  @Test
  void buy_animals_is_forbidden_if_no_enough_money_test() {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("monkey");
    animal.setPrice(new BigDecimal("500"));
    animal.setGender(Gender.MALE);
    Zoo zoo = new Zoo();

    List<Animal> animalsPerKind = new ArrayList<>();

    Animal animalPerKind = new Animal();
    animalPerKind.setGender(Gender.FEMALE);
    animalPerKind.setKind(animal.getKind());
    animalsPerKind.add(animalPerKind);

    when(animalDao.getCountOfAllAnimals(zoo.getZooId())).thenReturn(15);
    when(animalDao.getAllAnimalsByKind(zoo.getZooId(), "monkey")).thenReturn(animalsPerKind);
    when(zooDao.money(zoo.getZooId())).thenReturn(new BigDecimal("-100"));

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.buyAnimal(zoo.getZooId(), animal));

    assertEquals(NOT_ENOUGH_MONEY, exception.getMessage());
  }

  // showSortedAnimalsPerParam

  @Test
  void show_sorted_animals_with_valid_param_test() {
    Integer offset = 0;
    Integer size = 5;
    String color = "red";
    String gender = "male";
    String sortingParam = "animal_id";

    PageRequest pageRequest = PageRequest.of(offset, size);

    animalService.showSortedAnimalsPerParam(offset, size, color, gender, sortingParam);

    verify(animalDao).showSortedAnimalsPerParam(pageRequest, color, gender, sortingParam);
  }

  @Test
  void show_sorted_animals_with_invalid_param_test() {
    Integer offset = 0;
    Integer size = 5;
    String color = "red";
    String gender = "male";
    String sortingParam = "invalid_param";

    Exception exception = assertThrows(ApiRequestException.class, () ->
        animalService.showSortedAnimalsPerParam(offset, size, color, gender, sortingParam));

    assertEquals(INVALID_PARAMETER, exception.getMessage());
  }
}
