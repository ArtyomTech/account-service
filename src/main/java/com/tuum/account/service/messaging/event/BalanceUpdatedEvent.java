package com.tuum.account.service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceUpdatedEvent(
    Long balanceId,
    Long accountId,
    String currency,
    BigDecimal previousAmount,
    BigDecimal newAmount,
    LocalDateTime timestamp) {}
