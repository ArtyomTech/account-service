package com.tuum.account.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateTransactionRequest(
    @NotNull(message = "Account ID missing") Long accountId,
    @NotNull(message = "Amount missing") @Positive(message = "Invalid amount") BigDecimal amount,
    @NotNull(message = "Currency missing") CurrencyDto currency,
    @NotNull(message = "Direction missing") TransactionDirectionDto direction,
    @NotBlank(message = "Description missing") String description) {}
