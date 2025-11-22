package com.tuum.account.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tuum.account.service.PostgreSQLTestBase;
import com.tuum.account.service.domain.Account;
import com.tuum.account.service.domain.Balance;
import com.tuum.account.service.domain.Currency;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BalanceMapperTest extends PostgreSQLTestBase {

  private static final Long TEST_CUSTOMER_ID = 123L;

  @Autowired private BalanceMapper balanceMapper;
  @Autowired private AccountMapper accountMapper;

  @Test
  void shouldInsertBalance() {
    Account account = Account.builder().customerId(TEST_CUSTOMER_ID).country("EE").build();
    accountMapper.insertAccount(account);
    Balance balance =
        Balance.builder()
            .accountId(account.getAccountId())
            .availableAmount(new BigDecimal("100.50"))
            .currency(Currency.EUR)
            .build();

    balanceMapper.insertBalance(balance);

    assertThat(balance.getBalanceId()).isNotNull();
    assertThat(balance)
        .satisfies(
            b -> {
              assertThat(b.getAccountId()).isEqualTo(account.getAccountId());
              assertThat(b.getCurrency()).isEqualTo(Currency.EUR);
              assertThat(b.getAvailableAmount()).isEqualByComparingTo("100.50");
            });
  }

  @Test
  void shouldUpdateBalance() {
    Account account = Account.builder().customerId(TEST_CUSTOMER_ID).country("EE").build();
    accountMapper.insertAccount(account);
    Balance balance =
        Balance.builder()
            .accountId(account.getAccountId())
            .availableAmount(new BigDecimal("100.00"))
            .currency(Currency.USD)
            .build();
    balanceMapper.insertBalance(balance);

    balance.setAvailableAmount(new BigDecimal("200.00"));
    balanceMapper.updateBalance(balance);

    Account accountWithBalances = accountMapper.findById(account.getAccountId()).orElseThrow();
    assertThat(accountWithBalances.getBalances())
        .hasSize(1)
        .first()
        .satisfies(
            b -> {
              assertThat(b.getBalanceId()).isNotNull();
              assertThat(b.getAvailableAmount()).isEqualByComparingTo("200.00");
              assertThat(b.getCurrency()).isEqualTo(Currency.USD);
            });
  }
}
