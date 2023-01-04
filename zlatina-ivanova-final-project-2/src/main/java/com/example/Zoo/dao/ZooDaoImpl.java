package com.example.Zoo.dao;

import com.example.Zoo.dto.AnimalRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class ZooDaoImpl implements ZooDao {

  private final NamedParameterJdbcOperations jdbcOperations;

  public ZooDaoImpl(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public void decreaseFreePlaces(Long zooId) {

    String sql =
        " UPDATE zoo                                    "
            + " SET available_places=available_places-1 "
            + " WHERE zoo_id=:zoo_id                    ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id", zooId);

    jdbcOperations.update(sql, params);
  }

  @Override
  public void increaseFreePlaces(Long zooId) {
    String sql =
        " UPDATE zoo                                    "
            + " SET available_places=available_places+1 "
            + " WHERE zoo_id=:zoo_id                    ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id", zooId);

    jdbcOperations.update(sql, params);
  }

  @Override
  public Optional<Integer> getFreePlaces(Long zooId) {

    String sql =
        " SELECT available_places    "
            + " FROM zoo             "
            + " WHERE zoo_id=:zoo_id ";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id", zooId);

    try {
      return Optional.ofNullable(jdbcOperations.queryForObject(sql, params, Integer.class));
    }catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  @Override
  public void decreaseMoney(Long zooId, AnimalRequest animalRequest) {
    String sql =
        " UPDATE zoo                 "
            + " SET money=:money     "
            + " WHERE zoo_id=:zoo_id ";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("money", money(zooId).subtract(animalRequest.getPrice()));
    params.addValue("zoo_id", zooId);

    jdbcOperations.update(sql, params);
  }

  @Override
  public void increaseMoney(Long zooId, BigDecimal price) {

    String sql =
        " UPDATE zoo                 "
            + " SET money=:money     "
            + " WHERE zoo_id=:zoo_id ";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("money", money(zooId).add(price));
    params.addValue("zoo_id", zooId);

    jdbcOperations.update(sql, params);

  }

  @Override
  public BigDecimal money(Long zooId) {

    String sql =
        " SELECT money               "
            + " FROM zoo             "
            + " WHERE zoo_id=:zoo_id ";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("zoo_id", zooId);

    return jdbcOperations.queryForObject(sql, params, BigDecimal.class);
  }

}
