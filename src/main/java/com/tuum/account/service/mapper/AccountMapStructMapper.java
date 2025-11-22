package com.tuum.account.service.mapper;

import com.tuum.account.service.domain.Account;
import com.tuum.account.service.dto.AccountResponse;
import com.tuum.account.service.dto.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = BalanceMapStructMapper.class)
public interface AccountMapStructMapper {

  @Mapping(target = "accountId", ignore = true)
  @Mapping(target = "balances", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Account toAccount(CreateAccountRequest request);

  AccountResponse toAccountResponse(Account account);
}
