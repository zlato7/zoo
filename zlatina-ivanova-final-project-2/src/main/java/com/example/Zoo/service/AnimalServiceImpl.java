package com.example.Zoo.service;

import static com.example.Zoo.dto.Color.NO_COLOR;
import static com.example.Zoo.dto.Color.PINK;
import static com.example.Zoo.dto.Color.YELLOW;
import static com.example.Zoo.dto.Diet.HERBIVORE;
import static com.example.Zoo.dto.Diet.PREDATOR;
import static com.example.Zoo.dto.Gender.MALE;
import static com.example.Zoo.exception.ExceptionMessages.CANT_PAINT_PINK;
import static com.example.Zoo.exception.ExceptionMessages.DUPLICATE_ANIMAL_FORBIDDEN;
import static com.example.Zoo.exception.ExceptionMessages.FORBIDDEN_COLOR;
import static com.example.Zoo.exception.ExceptionMessages.INVALID_PARAMETER;
import static com.example.Zoo.exception.ExceptionMessages.MORE_THAN_ONE_COUPLE_FORBIDDEN;
import static com.example.Zoo.exception.ExceptionMessages.NOT_ENOUGH_MONEY;
import static com.example.Zoo.exception.ExceptionMessages.NOT_EXISTING_ANIMAL;
import static com.example.Zoo.exception.ExceptionMessages.NOT_EXISTING_ZOO;
import static com.example.Zoo.exception.ExceptionMessages.REACHED_MAXIMUM_KINDS;
import static com.example.Zoo.exception.ExceptionMessages.SELL_MALE_ANIMAL_FORBIDDEN;
import static com.example.Zoo.exception.ExceptionMessages.SELL_PAINTED_ANIMALS_FORBIDDEN;
import static com.example.Zoo.exception.ExceptionMessages.THE_ZOO_IS_FULL;

import com.example.Zoo.dao.AnimalDao;
import com.example.Zoo.dao.ZooDao;
import com.example.Zoo.dto.Animal;
import com.example.Zoo.dto.AnimalRequest;
import com.example.Zoo.dto.Color;
import com.example.Zoo.exception.ApiRequestException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnimalServiceImpl implements AnimalService {

  private final AnimalDao animalDao;
  private final ZooDao zooDao;

  private static Set<String>attributes=new HashSet<>();
  static {
    attributes.add("animal_id");
    attributes.add("kind");
    attributes.add("diet");
    attributes.add("gender");
    attributes.add("color");
    attributes.add("price");
    attributes.add("zoo_id_fk");
  }

  public AnimalServiceImpl(AnimalDao animalDao, ZooDao zooDao) {
    this.animalDao = animalDao;
    this.zooDao = zooDao;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
  public void buyAnimal(Long zooId, AnimalRequest animalRequest) {

    int animalsCount = animalDao.getCountOfAllAnimals(zooId);
    if (animalsCount >= 20) {
      throw new ApiRequestException(THE_ZOO_IS_FULL);
    }
    List<Animal> animalsFromRequestedKind = animalDao.getAllAnimalsByKind(zooId,
        animalRequest.getKind());

    if (animalsFromRequestedKind.size() >= 2) {
      throw new ApiRequestException(MORE_THAN_ONE_COUPLE_FORBIDDEN);
    }
    if (animalsFromRequestedKind.size() == 1 && animalsFromRequestedKind.get(0).getGender().name()
        .equalsIgnoreCase(animalRequest.getGender().name())) {

      throw new ApiRequestException(
          String.format(DUPLICATE_ANIMAL_FORBIDDEN, animalRequest.getGender().name().toLowerCase(),
              animalRequest.getKind()));
    }
    if (animalsFromRequestedKind.size() == 0) {

      int freePlaces = zooDao.getFreePlaces(zooId)
          .orElseThrow(() -> new ApiRequestException(NOT_EXISTING_ZOO));
      if (freePlaces == 0) {
        throw new ApiRequestException(REACHED_MAXIMUM_KINDS);
      } else {
        zooDao.decreaseFreePlaces(zooId);
      }
    }

    animalDao.buyAnimal(zooId, animalRequest);
    zooDao.decreaseMoney(zooId, animalRequest);

    if (zooDao.money(zooId).compareTo(BigDecimal.ZERO) < 0) {
      throw new ApiRequestException(NOT_ENOUGH_MONEY);
    }

  }

  @Override
  public List<Animal> getAllAnimals(Long zooId) {
    return animalDao.getAllAnimals(zooId);
  }

  @Override
  public void paintAnimal(Long animalId, String color) {

    if (!Color.isValid(color)) {
      throw new ApiRequestException(INVALID_PARAMETER);
    }
    Animal animal = animalDao.getAnimal(animalId)
        .orElseThrow(() -> new ApiRequestException(String.format(NOT_EXISTING_ANIMAL, animalId)));

    if (color.equalsIgnoreCase(PINK.name())) {

      List<Animal> animalsFromRequestedColor = animalDao.getAllAnimalsPerColorInTheZoo(
          animal.getZooId(), color);

      if (animalsFromRequestedColor.size() >= 2) {
        throw new ApiRequestException(CANT_PAINT_PINK);
      }
    }

    if (YELLOW.name().equalsIgnoreCase(color) &&
        animal.getDiet().name().equalsIgnoreCase(HERBIVORE.name())) {

      throw new ApiRequestException(String.format(FORBIDDEN_COLOR, HERBIVORE.name().toLowerCase(),
          YELLOW.name().toLowerCase()));
    }

    if (color.equalsIgnoreCase(PINK.name()) &&
        animal.getDiet().name().equalsIgnoreCase(PREDATOR.name())) {

      throw new ApiRequestException(
          String.format(FORBIDDEN_COLOR, PREDATOR.name().toLowerCase(), PINK.name().toLowerCase()));
    }

    animalDao.paintAnimal(animalId, color);
  }

  @Override
  public void sellAnimal(Long animalId) {

    Animal animal = animalDao.getAnimal(animalId)
        .orElseThrow(() -> new ApiRequestException(String.format(NOT_EXISTING_ANIMAL, animalId)));

    if (animal.getColor().name().equalsIgnoreCase(NO_COLOR.name())) {

      List<Animal> animalsPerKind = animalDao.getAllAnimalsByKind(animal.zooId, animal.getKind());

      if (animalsPerKind.size() >= 2 && animal.getGender().name().equalsIgnoreCase(MALE.name())) {
        throw new ApiRequestException(
            String.format(SELL_MALE_ANIMAL_FORBIDDEN, animal.getKind().toLowerCase()));
      }

      animalDao.sellAnimal(animalId);
      zooDao.increaseMoney(animal.getZooId(), animal.getPrice());

      animalsPerKind = animalDao.getAllAnimalsByKind(animal.zooId,
          animal.getKind());
      if (animalsPerKind.size() == 0) {
        zooDao.increaseFreePlaces(animal.zooId);
      }
    } else {
      throw new ApiRequestException(SELL_PAINTED_ANIMALS_FORBIDDEN);
    }
  }


  @Override
  public List<Animal> showSortedAnimalsPerParam(Integer offset, Integer size, String color,
      String gender, String parameter) {

    PageRequest pageRequest = PageRequest.of(offset, size);

    String sortingParam = checkColumnNameFromAnimalData(parameter);

    return animalDao.showSortedAnimalsPerParam(pageRequest, color, gender, sortingParam);
  }

  private String checkColumnNameFromAnimalData(String parameter) {

    for (String attribute : attributes) {
      if (parameter.equalsIgnoreCase(attribute)){
        return attribute;
      }
    }
    throw new ApiRequestException(INVALID_PARAMETER);
  }
}