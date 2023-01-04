package com.example.Zoo.controller;

import static com.example.Zoo.common.Urls.BUY_OR_SHOW_ANIMALS;
import static com.example.Zoo.dto.Gender.FEMALE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.Zoo.dto.AnimalRequest;
import com.example.Zoo.dto.Diet;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AnimalControllerTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void buy_animal_with_valid_data_test() throws Exception {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("test kind");
    animal.setDiet(Diet.HERBIVORE);
    animal.setGender(FEMALE);
    animal.setPrice(new BigDecimal("100"));

    mockMvc
        .perform(post(BUY_OR_SHOW_ANIMALS, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(animal)))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  void buy_animal_with_invalid_kind_length_forbidden_test() throws Exception {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("m");
    animal.setDiet(Diet.HERBIVORE);
    animal.setGender(FEMALE);
    animal.setPrice(new BigDecimal("100"));

    mockMvc
        .perform(post(BUY_OR_SHOW_ANIMALS, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(animal)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void buy_animal_with_high_price_forbidden_test() throws Exception {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("test kind");
    animal.setDiet(Diet.HERBIVORE);
    animal.setGender(FEMALE);
    animal.setPrice(new BigDecimal("10000"));

    mockMvc
        .perform(post(BUY_OR_SHOW_ANIMALS, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(animal)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void buy_animal_with_low_price_forbidden_test() throws Exception {
    AnimalRequest animal = new AnimalRequest();
    animal.setKind("test kind");
    animal.setDiet(Diet.HERBIVORE);
    animal.setGender(FEMALE);
    animal.setPrice(BigDecimal.ZERO);

    mockMvc
        .perform(post(BUY_OR_SHOW_ANIMALS, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(animal)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void get_all_animals_test() throws Exception {
    mockMvc
        .perform(get(BUY_OR_SHOW_ANIMALS, 1)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
