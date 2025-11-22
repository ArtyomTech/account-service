package com.tuum.account.service.dto;

import java.math.BigDecimal;

public record TransactionResponse(
    Long transactionId,
    Long accountId,
    BigDecimal amount,
    CurrencyDto currency,
    TransactionDirectionDto direction,
    String description,
    BigDecimal balanceAfterTransaction) {}
