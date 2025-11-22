package com.tuum.account.service.mapper;

import com.tuum.account.service.domain.Account;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
  void insertAccount(Account account);

  Optional<Account> findById(Long accountId);
}
