package com.tuum.account.service.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreatedEvent(
    Long transactionId,
    Long accountId,
    BigDecimal amount,
    String currency,
    String direction,
    String description,
    LocalDateTime timestamp) {}
