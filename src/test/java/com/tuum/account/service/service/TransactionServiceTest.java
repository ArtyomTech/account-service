package com.tuum.account.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tuum.account.service.domain.Account;
import com.tuum.account.service.domain.Balance;
import com.tuum.account.service.domain.Currency;
import com.tuum.account.service.domain.Transaction;
import com.tuum.account.service.domain.TransactionDirection;
import com.tuum.account.service.dto.CreateTransactionRequest;
import com.tuum.account.service.dto.CurrencyDto;
import com.tuum.account.service.dto.TransactionDirectionDto;
import com.tuum.account.service.exception.InsufficientFundsException;
import com.tuum.account.service.exception.InvalidCurrencyException;
import com.tuum.account.service.mapper.BalanceMapper;
import com.tuum.account.service.mapper.TransactionMapper;
import com.tuum.account.service.messaging.EventPublisher;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  private static final Long TEST_ACCOUNT_ID = 1L;
  private static final Long TEST_TRANSACTION_ID = 100L;
  private static final BigDecimal INITIAL_BALANCE = new BigDecimal("100.00");
  private static final BigDecimal TRANSACTION_AMOUNT = new BigDecimal("50.00");

  @Mock private AccountService accountService;
  @Mock private BalanceMapper balanceMapper;
  @Mock private TransactionMapper transactionMapper;
  @Mock private EventPublisher eventPublisher;

  @InjectMocks private TransactionService transactionService;

  @Test
  void shouldCreateIncomingTransactionSuccessfully() {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TEST_ACCOUNT_ID,
            TRANSACTION_AMOUNT,
            CurrencyDto.EUR,
            TransactionDirectionDto.IN,
            "Test deposit");
    Balance balance =
        Balance.builder()
            .balanceId(1L)
            .accountId(TEST_ACCOUNT_ID)
            .availableAmount(INITIAL_BALANCE)
            .currency(Currency.EUR)
            .build();
    Account account =
        Account.builder().accountId(TEST_ACCOUNT_ID).balances(List.of(balance)).build();

    when(accountService.getAccount(TEST_ACCOUNT_ID)).thenReturn(account);
    doAnswer(
            invocation -> {
              Transaction tx = invocation.getArgument(0);
              tx.setTransactionId(TEST_TRANSACTION_ID);
              return null;
            })
        .when(transactionMapper)
        .insertTransaction(any(Transaction.class));

    Transaction result = transactionService.createTransaction(request);

    assertThat(result.getTransactionId()).isEqualTo(TEST_TRANSACTION_ID);
    assertThat(result.getAmount()).isEqualByComparingTo(TRANSACTION_AMOUNT);
    assertThat(result.getDirection()).isEqualTo(TransactionDirection.IN);
    assertThat(result.getBalanceAfterTransaction())
        .isEqualByComparingTo(INITIAL_BALANCE.add(TRANSACTION_AMOUNT));

    verify(balanceMapper).updateBalance(any(Balance.class));
    verify(transactionMapper).insertTransaction(any(Transaction.class));
    verify(eventPublisher).publish(eq("transaction.created"), any());
    verify(eventPublisher).publish(eq("balance.updated"), any());
  }

  @Test
  void shouldCreateOutgoingTransactionSuccessfully() {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TEST_ACCOUNT_ID,
            TRANSACTION_AMOUNT,
            CurrencyDto.EUR,
            TransactionDirectionDto.OUT,
            "Test withdrawal");
    Balance balance =
        Balance.builder()
            .balanceId(1L)
            .accountId(TEST_ACCOUNT_ID)
            .availableAmount(INITIAL_BALANCE)
            .currency(Currency.EUR)
            .build();
    Account account =
        Account.builder().accountId(TEST_ACCOUNT_ID).balances(List.of(balance)).build();

    when(accountService.getAccount(TEST_ACCOUNT_ID)).thenReturn(account);
    doAnswer(
            invocation -> {
              Transaction tx = invocation.getArgument(0);
              tx.setTransactionId(TEST_TRANSACTION_ID);
              return null;
            })
        .when(transactionMapper)
        .insertTransaction(any(Transaction.class));

    Transaction result = transactionService.createTransaction(request);

    assertThat(result.getDirection()).isEqualTo(TransactionDirection.OUT);
    assertThat(result.getBalanceAfterTransaction())
        .isEqualByComparingTo(INITIAL_BALANCE.subtract(TRANSACTION_AMOUNT));
  }

  @Test
  void shouldThrowExceptionWhenInsufficientFunds() {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TEST_ACCOUNT_ID,
            new BigDecimal("200.00"),
            CurrencyDto.EUR,
            TransactionDirectionDto.OUT,
            "Test withdrawal");
    Balance balance =
        Balance.builder()
            .balanceId(1L)
            .accountId(TEST_ACCOUNT_ID)
            .availableAmount(INITIAL_BALANCE)
            .currency(Currency.EUR)
            .build();
    Account account =
        Account.builder().accountId(TEST_ACCOUNT_ID).balances(List.of(balance)).build();

    when(accountService.getAccount(TEST_ACCOUNT_ID)).thenReturn(account);

    assertThatThrownBy(() -> transactionService.createTransaction(request))
        .isInstanceOf(InsufficientFundsException.class)
        .hasMessage("Insufficient funds");

    verify(balanceMapper, never()).updateBalance(any());
    verify(transactionMapper, never()).insertTransaction(any());
  }

  @Test
  void shouldThrowExceptionWhenCurrencyNotFound() {
    CreateTransactionRequest request =
        new CreateTransactionRequest(
            TEST_ACCOUNT_ID,
            TRANSACTION_AMOUNT,
            CurrencyDto.USD,
            TransactionDirectionDto.IN,
            "Test deposit");
    Balance balance =
        Balance.builder()
            .balanceId(1L)
            .accountId(TEST_ACCOUNT_ID)
            .availableAmount(INITIAL_BALANCE)
            .currency(Currency.EUR)
            .build();
    Account account =
        Account.builder().accountId(TEST_ACCOUNT_ID).balances(List.of(balance)).build();

    when(accountService.getAccount(TEST_ACCOUNT_ID)).thenReturn(account);

    assertThatThrownBy(() -> transactionService.createTransaction(request))
        .isInstanceOf(InvalidCurrencyException.class)
        .hasMessage("Invalid currency");
  }

  @Test
  void shouldGetTransactionsSuccessfully() {
    List<Transaction> transactions =
        List.of(
            Transaction.builder().transactionId(1L).accountId(TEST_ACCOUNT_ID).build(),
            Transaction.builder().transactionId(2L).accountId(TEST_ACCOUNT_ID).build());

    Account account = Account.builder().accountId(TEST_ACCOUNT_ID).build();
    when(accountService.getAccount(TEST_ACCOUNT_ID)).thenReturn(account);
    when(transactionMapper.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(transactions);

    List<Transaction> result = transactionService.getTransactions(TEST_ACCOUNT_ID);

    assertThat(result).hasSize(2);
    verify(transactionMapper).findByAccountId(TEST_ACCOUNT_ID);
  }
}
