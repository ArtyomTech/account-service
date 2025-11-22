package com.tuum.account.service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  private Long transactionId;
  private Long accountId;
  private BigDecimal amount;
  private Currency currency;
  private TransactionDirection direction;
  private String description;
  private BigDecimal balanceAfterTransaction;
  private LocalDateTime createdAt;
}
