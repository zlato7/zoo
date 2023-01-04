package com.example.Zoo.dao;

import com.example.Zoo.dto.Animal;
import com.example.Zoo.dto.AnimalRequest;
import com.example.Zoo.dto.Color;
import com.example.Zoo.dto.Diet;
import com.example.Zoo.dto.Gender;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class AnimalDaoImpl implements AnimalDao {

  private final NamedParameterJdbcOperations jdbcOperations;

  public AnimalDaoImpl(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public void buyAnimal(Long zooId, AnimalRequest animalRequest) {

    String sql =
        " INSERT INTO animals "
            + " (kind,        "
            + " diet,         "
            + " gender,       "
            + " price,        "
            + " zoo_id_fk)    "
            + " VALUES        "
            + " (:kind,       "
            + " :diet,        "
            + " :gender,      "
            + " :price,       "
            + " :zoo_id_fk)   ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("kind", animalRequest.getKind());
    params.addValue("diet", animalRequest.getDiet().getText().toUpperCase());
    params.addValue("gender", animalRequest.getGender().getText().toUpperCase());
    params.addValue("price", animalRequest.getPrice());
    params.addValue("zoo_id_fk", zooId);

    jdbcOperations.update(sql, params);
  }

  @Override
  public Optional<Animal> getAnimal(Long id) {
    String sql =
        " SELECT                           "
            + " animal_id,                 "
            + " kind,                      "
            + " diet,                      "
            + " gender,                    "
            + " color,                     "
            + " price,                     "
            + " zoo_id_fk                  "
            + " FROM animals               "
            + " WHERE animal_id=:animal_id ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("animal_id", id);

    try {
      return Optional.ofNullable(jdbcOperations.queryForObject(sql, params,
          (rs, rowNum) -> {
            Animal animal = new Animal();

            animal.setAnimalId(rs.getLong("animal_id"));
            animal.setKind(rs.getString("kind"));
            animal.setDiet(rs.getObject("diet", Diet.class));
            animal.setGender(rs.getObject("gender", Gender.class));
            animal.setColor(rs.getObject("color", Color.class));
            animal.setPrice(rs.getBigDecimal("price"));
            animal.setZooId(rs.getLong("zoo_id_fk"));

            return animal;
          }));
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  @Override
  public List<Animal> getAllAnimals(Long zooId) {

    String sql =
        " SELECT                           "
            + " animal_id,                 "
            + " kind,                      "
            + " diet,                      "
            + " gender,                    "
            + " color,                     "
            + " price,                     "
            + " zoo_id_fk                  "
            + " FROM animals               "
            + " WHERE zoo_id_fk=:zoo_id_fk ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id_fk", zooId);

    List<Animal> animals = jdbcOperations.query(sql, params,
        new ResultSetExtractor<List<Animal>>() {
          @Override
          public List<Animal> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<Animal> animalList = new ArrayList<>();
            while (rs.next()) {

              Animal animal = new Animal();

              animal.setAnimalId(rs.getLong("animal_id"));
              animal.setKind(rs.getString("kind"));
              animal.setDiet(rs.getObject("diet", Diet.class));
              animal.setGender(rs.getObject("gender", Gender.class));
              animal.setColor(rs.getObject("color", Color.class));
              animal.setPrice(rs.getBigDecimal("price"));
              animal.setZooId(rs.getLong("zoo_id_fk"));

              animalList.add(animal);

            }
            return animalList;
          }
        });
    return animals;
  }

  @Override
  public List<Animal> getAllAnimalsByKind(Long zooId, String kind) {

    String sql =
        " SELECT                         "
            + " kind,                    "
            + " diet,                    "
            + " gender,                  "
            + " price,                   "
            + " zoo_id_fk                "
            + " FROM animals             "
            + " WHERE kind=:kind         "
            + " AND zoo_id_fk=:zoo_id_fk ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("kind", kind);
    params.addValue("zoo_id_fk", zooId);

    return jdbcOperations.query(sql, params,
        rs -> {
          List<Animal> animals = new ArrayList<>();

          while (rs.next()) {
            Animal animal = new Animal();

            animal.setKind(rs.getString("kind"));
            animal.setDiet(rs.getObject("diet", Diet.class));
            animal.setGender(rs.getObject("gender", Gender.class));
            animal.setPrice(rs.getBigDecimal("price"));

            animals.add(animal);
          }

          return animals;
        });
  }

  @Override
  public int getCountOfAllAnimals(Long zooId) {
    String sql =
        " SELECT COUNT(*)                    "
            + " FROM animals                 "
            + " WHERE zoo_id_fk = :zoo_id_fk ";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id_fk", zooId);

    return jdbcOperations.queryForObject(sql, params, Integer.class);
  }

  @Override
  public void paintAnimal(Long animalId, String color) {

    String sql =
        " UPDATE animals                   "
            + " SET color=:color           "
            + " WHERE animal_id=:animal_id ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("color", Color.valueOf(color.toUpperCase()).toString());
    params.addValue("animal_id", animalId);

    jdbcOperations.update(sql, params);
  }

  @Override
  public List<Animal> getAllAnimalsPerColorInTheZoo(Long zooId, String color) {

    String sql =
        " SELECT "
            + " animal_id,                 "
            + " kind,                      "
            + " diet,                      "
            + " gender,                    "
            + " color,                     "
            + " price,                     "
            + " zoo_id_fk                  "
            + " FROM animals               "
            + " WHERE zoo_id_fk=:zoo_id_fk "
            + " AND color=:color           ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id_fk", zooId);
    params.addValue("color", Color.valueOf(color.toUpperCase()).toString());

    List<Animal> animalsPerColor = jdbcOperations.query(sql, params,
        new ResultSetExtractor<List<Animal>>() {
          @Override
          public List<Animal> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<Animal> animals = new ArrayList<>();

            while (rs.next()) {
              Animal animal = new Animal();

              animal.setAnimalId(rs.getLong("animal_id"));
              animal.setKind(rs.getString("kind"));
              animal.setDiet(rs.getObject("diet", Diet.class));
              animal.setGender(rs.getObject("gender", Gender.class));
              animal.setColor(rs.getObject("color", Color.class));
              animal.setPrice(rs.getBigDecimal("price"));
              animal.setZooId(rs.getLong("zoo_id_fk"));

              animals.add(animal);
            }

            return animals;
          }
        });

    return animalsPerColor;
  }

  @Override
  public void sellAnimal(Long animalId) {

    String sql =
        " DELETE "
            + " FROM animals               "
            + " WHERE animal_id=:animal_id ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("animal_id", animalId);

    jdbcOperations.update(sql, params);
  }

  @Override
  public List<Animal> showSortedAnimalsPerParam(PageRequest pageRequest, String color,
      String gender, String sortingParam) {

    String sql =
        " SELECT                                "
            + " animal_id,                      "
            + " kind,                           "
            + " diet,                           "
            + " gender,                         "
            + " color,                          "
            + " price,                          "
            + " zoo_id_fk                       "
            + " FROM animals                    "
            + " WHERE 1=1                       "
            + filterBy(color," AND color=:color ")
            + filterBy(gender," AND gender=:gender ")
            + " ORDER BY %s                     "
            + " OFFSET :offset                  "
            + " ROWS FETCH NEXT :next ROWS ONLY ";

    String sqlWithSortingParam = String.format(sql, sortingParam);

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("gender",
        gender != null ? Gender.valueOf(gender.toUpperCase()).toString() : "");
    params.addValue("color", color != null ? Color.valueOf(color.toUpperCase()).toString() : "");
    params.addValue("sortingParam", sortingParam);
    params.addValue("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
    params.addValue("next", pageRequest.getPageSize());

    List<Animal> sortedAnimalsPerGender = jdbcOperations.query(sqlWithSortingParam, params,
        new ResultSetExtractor<List<Animal>>() {
          @Override
          public List<Animal> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<Animal> animals = new ArrayList<>();

            while (rs.next()) {

              Animal animal = new Animal();

              animal.setAnimalId(rs.getLong("animal_id"));
              animal.setKind(rs.getString("kind"));
              animal.setDiet(rs.getObject("diet", Diet.class));
              animal.setGender(rs.getObject("gender", Gender.class));
              animal.setColor(rs.getObject("color", Color.class));
              animal.setPrice(rs.getBigDecimal("price"));
              animal.setZooId(rs.getLong("zoo_id_fk"));

              animals.add(animal);
            }
            return animals;
          }
        });
    return sortedAnimalsPerGender;
  }

  public static String filterBy(String criteria, String statement) {
    return criteria != null ? statement : "";
  }
}
