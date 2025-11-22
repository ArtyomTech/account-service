package com.tuum.account.service.mapper;

import com.tuum.account.service.domain.Balance;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BalanceMapper {
  void insertBalance(Balance balance);

  void updateBalance(Balance balance);
}
