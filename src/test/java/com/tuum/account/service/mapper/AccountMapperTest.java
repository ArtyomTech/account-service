package com.tuum.account.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tuum.account.service.PostgreSQLTestBase;
import com.tuum.account.service.domain.Account;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountMapperTest extends PostgreSQLTestBase {

  private static final Long TEST_CUSTOMER_ID = 123L;
  private static final String TEST_COUNTRY = "EE";

  @Autowired private AccountMapper accountMapper;

  @Test
  void shouldInsertAndFindAccount() {
    Account account = Account.builder().customerId(TEST_CUSTOMER_ID).country(TEST_COUNTRY).build();

    accountMapper.insertAccount(account);
    Long accountId = account.getAccountId();

    assertThat(accountId).isNotNull();
    assertThat(accountMapper.findById(accountId))
        .isPresent()
        .get()
        .satisfies(
            a -> {
              assertThat(a.getCustomerId()).isEqualTo(TEST_CUSTOMER_ID);
              assertThat(a.getCountry()).isEqualTo(TEST_COUNTRY);
            });
  }

  @Test
  void shouldReturnEmptyWhenAccountNotFound() {
    assertThat(accountMapper.findById(999L)).isEmpty();
  }
}
