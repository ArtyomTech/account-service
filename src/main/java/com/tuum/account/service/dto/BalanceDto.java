package com.tuum.account.service.dto;

import java.math.BigDecimal;

public record BalanceDto(CurrencyDto currency, BigDecimal availableAmount) {}
