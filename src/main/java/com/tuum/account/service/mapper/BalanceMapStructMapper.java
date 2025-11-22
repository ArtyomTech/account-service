package com.tuum.account.service.mapper;

import com.tuum.account.service.domain.Balance;
import com.tuum.account.service.dto.BalanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BalanceMapStructMapper {
  BalanceDto toBalance(Balance balance);
}
