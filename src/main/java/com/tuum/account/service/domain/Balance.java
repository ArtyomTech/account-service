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
public class Balance {
  private Long balanceId;
  private Long accountId;
  private BigDecimal availableAmount;
  private Currency currency;
  private LocalDateTime createdAt;
}
