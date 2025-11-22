package com.tuum.account.service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceCreatedEvent(
    Long balanceId,
    Long accountId,
    String currency,
    BigDecimal initialAmount,
    LocalDateTime timestamp) {}
