package com.tuum.account.service.service;

import com.tuum.account.service.domain.Account;
import com.tuum.account.service.domain.Balance;
import com.tuum.account.service.domain.Currency;
import com.tuum.account.service.dto.CreateAccountRequest;
import com.tuum.account.service.exception.AccountNotFoundException;
import com.tuum.account.service.mapper.AccountMapStructMapper;
import com.tuum.account.service.mapper.AccountMapper;
import com.tuum.account.service.mapper.BalanceMapper;
import com.tuum.account.service.messaging.EventPublisher;
import com.tuum.account.service.messaging.event.AccountCreatedEvent;
import com.tuum.account.service.messaging.event.BalanceCreatedEvent;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final AccountMapper accountMapper;
  private final BalanceMapper balanceMapper;
  private final AccountMapStructMapper accountMapStructMapper;
  private final EventPublisher eventPublisher;

  @Transactional
  public Account createAccount(CreateAccountRequest request) {
    Account account = accountMapStructMapper.toAccount(request);
    accountMapper.insertAccount(account);

    List<Balance> balances = saveInitialBalances(account.getAccountId(), request);
    account.setBalances(balances);
    publishAccountCreatedEvent(account, request);

    return account;
  }

  public Account getAccount(Long accountId) {
    return accountMapper
        .findById(accountId)
        .orElseThrow(() -> new AccountNotFoundException("Account missing"));
  }

  private List<Balance> saveInitialBalances(Long accountId, CreateAccountRequest request) {
    return request.currencies().stream()
        .map(
            currency -> {
              Balance balance =
                  Balance.builder()
                      .accountId(accountId)
                      .availableAmount(BigDecimal.ZERO)
                      .currency(Currency.valueOf(currency.name()))
                      .build();
              balanceMapper.insertBalance(balance);
              publishBalanceCreatedEvent(balance);
              return balance;
            })
        .toList();
  }

  private void publishAccountCreatedEvent(Account account, CreateAccountRequest request) {
    AccountCreatedEvent event =
        new AccountCreatedEvent(
            account.getAccountId(),
            account.getCustomerId(),
            account.getCountry(),
            request.currencies().stream().map(Enum::name).toList(),
            account.getCreatedAt());
    eventPublisher.publish("account.created", event);
  }

  private void publishBalanceCreatedEvent(Balance balance) {
    BalanceCreatedEvent event =
        new BalanceCreatedEvent(
            balance.getBalanceId(),
            balance.getAccountId(),
            balance.getCurrency().name(),
            balance.getAvailableAmount(),
            balance.getCreatedAt());
    eventPublisher.publish("balance.created", event);
  }
}
