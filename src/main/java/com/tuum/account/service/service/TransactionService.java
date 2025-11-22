package com.tuum.account.service.service;

import com.tuum.account.service.domain.Account;
import com.tuum.account.service.domain.Balance;
import com.tuum.account.service.domain.Currency;
import com.tuum.account.service.domain.Transaction;
import com.tuum.account.service.domain.TransactionDirection;
import com.tuum.account.service.dto.CreateTransactionRequest;
import com.tuum.account.service.exception.*;
import com.tuum.account.service.mapper.BalanceMapper;
import com.tuum.account.service.mapper.TransactionMapper;
import com.tuum.account.service.messaging.EventPublisher;
import com.tuum.account.service.messaging.event.BalanceUpdatedEvent;
import com.tuum.account.service.messaging.event.TransactionCreatedEvent;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountService accountService;
  private final BalanceMapper balanceMapper;
  private final TransactionMapper transactionMapper;
  private final EventPublisher eventPublisher;

  @Transactional
  public Transaction createTransaction(CreateTransactionRequest request) {
    Currency currency = Currency.valueOf(request.currency().name());
    TransactionDirection direction = TransactionDirection.valueOf(request.direction().name());

    Account account = accountService.getAccount(request.accountId());
    Balance balance = findBalanceForCurrency(account, currency);
    BigDecimal newBalance =
        calculateNewBalance(balance.getAvailableAmount(), request.amount(), direction);
    updateBalance(balance, newBalance);

    Transaction transaction = saveTransaction(request, newBalance, currency, direction);
    publishTransactionCreatedEvent(transaction);

    return transaction;
  }

  public List<Transaction> getTransactions(Long accountId) {
    accountService.getAccount(accountId);
    return transactionMapper.findByAccountId(accountId);
  }

  private Balance findBalanceForCurrency(Account account, Currency currency) {
    return account.getBalances().stream()
        .filter(b -> b.getCurrency().equals(currency))
        .findFirst()
        .orElseThrow(() -> new InvalidCurrencyException("Invalid currency"));
  }

  private BigDecimal calculateNewBalance(
      BigDecimal current, BigDecimal amount, TransactionDirection direction) {
    BigDecimal newBalance =
        direction == TransactionDirection.IN ? current.add(amount) : current.subtract(amount);
    if (direction == TransactionDirection.OUT && newBalance.signum() < 0) {
      throw new InsufficientFundsException("Insufficient funds");
    }

    return newBalance;
  }

  private void updateBalance(Balance balance, BigDecimal newAmount) {
    BigDecimal previousAmount = balance.getAvailableAmount();
    balance.setAvailableAmount(newAmount);
    balanceMapper.updateBalance(balance);

    publishBalanceUpdatedEvent(balance, previousAmount, newAmount);
  }

  private Transaction saveTransaction(
      CreateTransactionRequest request,
      BigDecimal balanceAfter,
      Currency currency,
      TransactionDirection direction) {
    Transaction transaction =
        Transaction.builder()
            .accountId(request.accountId())
            .amount(request.amount())
            .currency(currency)
            .direction(direction)
            .description(request.description())
            .balanceAfterTransaction(balanceAfter)
            .build();
    transactionMapper.insertTransaction(transaction);
    return transaction;
  }

  private void publishTransactionCreatedEvent(Transaction transaction) {
    TransactionCreatedEvent event =
        new TransactionCreatedEvent(
            transaction.getTransactionId(),
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getCurrency().name(),
            transaction.getDirection().name(),
            transaction.getDescription(),
            transaction.getCreatedAt());
    eventPublisher.publish("transaction.created", event);
  }

  private void publishBalanceUpdatedEvent(
      Balance balance, BigDecimal previousAmount, BigDecimal newAmount) {
    BalanceUpdatedEvent event =
        new BalanceUpdatedEvent(
            balance.getBalanceId(),
            balance.getAccountId(),
            balance.getCurrency().name(),
            previousAmount,
            newAmount,
            balance.getCreatedAt());
    eventPublisher.publish("balance.updated", event);
  }
}
