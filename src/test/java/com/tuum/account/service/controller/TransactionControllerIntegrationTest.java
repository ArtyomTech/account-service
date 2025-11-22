package com.tuum.account.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.account.service.PostgreSQLTestBase;
import com.tuum.account.service.dto.CreateAccountRequest;
import com.tuum.account.service.dto.CreateTransactionRequest;
import com.tuum.account.service.dto.CurrencyDto;
import com.tuum.account.service.dto.TransactionDirectionDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
class TransactionControllerIntegrationTest extends PostgreSQLTestBase {

  private static final String ACCOUNTS_ENDPOINT = "/accounts";
  private static final String TRANSACTIONS_ENDPOINT = "/transactions";
  private static final Long TEST_CUSTOMER_ID = 123L;
  private static final String TEST_COUNTRY = "EE";
  private static final BigDecimal DEPOSIT_AMOUNT = new BigDecimal("100.00");
  private static final BigDecimal WITHDRAWAL_AMOUNT = new BigDecimal("50.00");

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private Long accountId;

  @BeforeEach
  void setUp() throws Exception {
    CreateAccountRequest accountRequest =
        new CreateAccountRequest(TEST_CUSTOMER_ID, TEST_COUNTRY, List.of(CurrencyDto.EUR));
    MvcResult result =
        mockMvc
            .perform(
                post(ACCOUNTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountRequest)))
            .andExpect(status().isOk())
            .andReturn();
    accountId =
        objectMapper.readTree(result.getResponse().getContentAsString()).get("accountId").asLong();
  }

  @Test
  void shouldCreateIncomingTransactionSuccessfully() throws Exception {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            accountId, DEPOSIT_AMOUNT, CurrencyDto.EUR, TransactionDirectionDto.IN, "Test deposit");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.transactionId").isNumber())
        .andExpect(jsonPath("$.accountId").value(accountId))
        .andExpect(jsonPath("$.amount").value(DEPOSIT_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.currency").value("EUR"))
        .andExpect(jsonPath("$.direction").value("IN"))
        .andExpect(jsonPath("$.balanceAfterTransaction").value(DEPOSIT_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.description").value("Test deposit"));
  }

  @Test
  void shouldCreateOutgoingTransactionSuccessfully() throws Exception {
    CreateTransactionRequest depositRequest =
        new CreateTransactionRequest(
            accountId,
            DEPOSIT_AMOUNT,
            CurrencyDto.EUR,
            TransactionDirectionDto.IN,
            "Initial deposit");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
        .andExpect(status().isOk());

    CreateTransactionRequest withdrawalRequest =
        new CreateTransactionRequest(
            accountId,
            WITHDRAWAL_AMOUNT,
            CurrencyDto.EUR,
            TransactionDirectionDto.OUT,
            "Test withdrawal");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawalRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.direction").value("OUT"))
        .andExpect(
            jsonPath("$.balanceAfterTransaction")
                .value(DEPOSIT_AMOUNT.subtract(WITHDRAWAL_AMOUNT).doubleValue()));
  }

  @Test
  void shouldReturnBadRequestWhenInsufficientFunds() throws Exception {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            accountId,
            new BigDecimal("1000.00"),
            CurrencyDto.EUR,
            TransactionDirectionDto.OUT,
            "Test withdrawal");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Insufficient funds"));
  }

  @Test
  void shouldReturnBadRequestWhenInvalidCurrency() throws Exception {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            accountId, DEPOSIT_AMOUNT, CurrencyDto.USD, TransactionDirectionDto.IN, "Test deposit");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Invalid currency"));
  }

  @Test
  void shouldReturnBadRequestWhenAmountIsNegative() throws Exception {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            accountId,
            new BigDecimal("-10.00"),
            CurrencyDto.EUR,
            TransactionDirectionDto.IN,
            "Test deposit");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Invalid amount"));
  }

  @Test
  void shouldReturnBadRequestWhenDescriptionMissing() throws Exception {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            accountId, DEPOSIT_AMOUNT, CurrencyDto.EUR, TransactionDirectionDto.IN, "");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Description missing"));
  }

  @Test
  void shouldGetTransactionsSuccessfully() throws Exception {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            accountId, DEPOSIT_AMOUNT, CurrencyDto.EUR, TransactionDirectionDto.IN, "Test deposit");
    mockMvc
        .perform(
            post(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    mockMvc
        .perform(get(TRANSACTIONS_ENDPOINT).param("accountId", accountId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].accountId").value(accountId));
  }

  @Test
  void shouldReturnNotFoundWhenGettingTransactionsForNonExistentAccount() throws Exception {
    mockMvc
        .perform(get(TRANSACTIONS_ENDPOINT).param("accountId", "999999"))
        .andExpect(status().isNotFound());
  }
}
