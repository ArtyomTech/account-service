package com.tuum.account.service.mapper;

import com.tuum.account.service.domain.Transaction;
import com.tuum.account.service.dto.CurrencyDto;
import com.tuum.account.service.dto.TransactionDirectionDto;
import com.tuum.account.service.dto.TransactionResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapStructMapper {
  TransactionResponse toTransactionResponse(Transaction transaction);

  List<TransactionResponse> toTransactionResponses(List<Transaction> transactions);

  default CurrencyDto mapCurrency(String currency) {
    return currency != null ? CurrencyDto.valueOf(currency) : null;
  }

  default TransactionDirectionDto mapDirection(String direction) {
    return direction != null ? TransactionDirectionDto.valueOf(direction) : null;
  }
}
