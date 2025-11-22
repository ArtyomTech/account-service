package com.tuum.account.service.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public record AccountCreatedEvent(
    Long accountId,
    Long customerId,
    String country,
    List<String> currencies,
    LocalDateTime timestamp) {}
