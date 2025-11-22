package com.tuum.account.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tuum.account.service.domain.Account;
import com.tuum.account.service.domain.Balance;
import com.tuum.account.service.dto.CreateAccountRequest;
import com.tuum.account.service.dto.CurrencyDto;
import com.tuum.account.service.exception.AccountNotFoundException;
import com.tuum.account.service.mapper.AccountMapStructMapper;
import com.tuum.account.service.mapper.AccountMapper;
import com.tuum.account.service.mapper.BalanceMapper;
import com.tuum.account.service.messaging.EventPublisher;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  private static final Long TEST_ACCOUNT_ID = 1L;
  private static final Long TEST_CUSTOMER_ID = 123L;
  private static final String TEST_COUNTRY = "EE";

  @Mock private AccountMapper accountMapper;
  @Mock private BalanceMapper balanceMapper;
  @Mock private EventPublisher eventPublisher;

  @Spy
  private AccountMapStructMapper accountMapStructMapper =
      Mappers.getMapper(AccountMapStructMapper.class);

  @InjectMocks private AccountService accountService;

  @Test
  void shouldCreateAccountSuccessfully() {
    CreateAccountRequest request =
        new CreateAccountRequest(TEST_CUSTOMER_ID, TEST_COUNTRY, List.of(CurrencyDto.EUR));
    doAnswer(
            invocation -> {
              Account acc = invocation.getArgument(0);
              acc.setAccountId(TEST_ACCOUNT_ID);
              return null;
            })
        .when(accountMapper)
        .insertAccount(any(Account.class));

    Account result = accountService.createAccount(request);

    assertThat(result.getAccountId()).isEqualTo(TEST_ACCOUNT_ID);
    assertThat(result.getCustomerId()).isEqualTo(TEST_CUSTOMER_ID);
    assertThat(result.getCountry()).isEqualTo(TEST_COUNTRY);
    assertThat(result.getBalances()).hasSize(1);

    verify(accountMapper).insertAccount(any(Account.class));
    verify(balanceMapper).insertBalance(any(Balance.class));
    verify(eventPublisher).publish(eq("account.created"), any());
    verify(eventPublisher).publish(eq("balance.created"), any());
  }

  @Test
  void shouldGetAccountSuccessfully() {
    Account account =
        Account.builder()
            .accountId(TEST_ACCOUNT_ID)
            .customerId(TEST_CUSTOMER_ID)
            .country(TEST_COUNTRY)
            .balances(Collections.emptyList())
            .build();
    when(accountMapper.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(account));

    Account result = accountService.getAccount(TEST_ACCOUNT_ID);

    assertThat(result.getAccountId()).isEqualTo(TEST_ACCOUNT_ID);
    verify(accountMapper).findById(TEST_ACCOUNT_ID);
  }

  @Test
  void shouldThrowExceptionWhenAccountNotFound() {
    when(accountMapper.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.getAccount(TEST_ACCOUNT_ID))
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessage("Account missing");
  }
}
