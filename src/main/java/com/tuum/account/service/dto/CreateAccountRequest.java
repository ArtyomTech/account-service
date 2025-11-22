package com.tuum.account.service.dto;

import com.tuum.account.service.validation.ValidCurrencies;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateAccountRequest(
    @NotNull(message = "Customer ID missing") Long customerId,
    @NotNull(message = "Country missing") String country,
    @NotEmpty @ValidCurrencies List<CurrencyDto> currencies) {}
