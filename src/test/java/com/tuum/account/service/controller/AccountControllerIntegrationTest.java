package com.tuum.account.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.account.service.PostgreSQLTestBase;
import com.tuum.account.service.dto.CreateAccountRequest;
import com.tuum.account.service.dto.CurrencyDto;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("null")
class AccountControllerIntegrationTest extends PostgreSQLTestBase {

  private static final String ACCOUNTS_ENDPOINT = "/accounts";
  private static final Long TEST_CUSTOMER_ID = 123L;
  private static final String TEST_COUNTRY = "EE";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldCreateAccountSuccessfully() throws Exception {
    CreateAccountRequest request =
        new CreateAccountRequest(
            TEST_CUSTOMER_ID, TEST_COUNTRY, List.of(CurrencyDto.EUR, CurrencyDto.USD));
    mockMvc
        .perform(
            post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").isNumber())
        .andExpect(jsonPath("$.customerId").value(TEST_CUSTOMER_ID))
        .andExpect(jsonPath("$.balances").isArray())
        .andExpect(jsonPath("$.balances.length()").value(2))
        .andExpect(jsonPath("$.balances[0].currency").exists())
        .andExpect(jsonPath("$.balances[0].availableAmount").value(0));
  }

  @Test
  void shouldGetAccountSuccessfully() throws Exception {
    CreateAccountRequest request =
        new CreateAccountRequest(TEST_CUSTOMER_ID, TEST_COUNTRY, List.of(CurrencyDto.EUR));
    MvcResult createResult =
        mockMvc
            .perform(
                post(ACCOUNTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    String accountId =
        objectMapper
            .readTree(createResult.getResponse().getContentAsString())
            .get("accountId")
            .asText();
    mockMvc
        .perform(get(ACCOUNTS_ENDPOINT + "/" + accountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(accountId))
        .andExpect(jsonPath("$.customerId").value(TEST_CUSTOMER_ID))
        .andExpect(jsonPath("$.balances").isArray());
  }

  @Test
  void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
    mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/999999")).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenCustomerIdMissing() throws Exception {
    CreateAccountRequest request =
        new CreateAccountRequest(null, TEST_COUNTRY, List.of(CurrencyDto.EUR));
    mockMvc
        .perform(
            post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Customer ID missing"));
  }

  @Test
  void shouldReturnBadRequestWhenCountryMissing() throws Exception {
    CreateAccountRequest request =
        new CreateAccountRequest(TEST_CUSTOMER_ID, null, List.of(CurrencyDto.EUR));
    mockMvc
        .perform(
            post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Country missing"));
  }

  @Test
  void shouldReturnBadRequestWhenCurrenciesEmpty() throws Exception {
    CreateAccountRequest request =
        new CreateAccountRequest(TEST_CUSTOMER_ID, TEST_COUNTRY, Collections.emptyList());
    mockMvc
        .perform(
            post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
